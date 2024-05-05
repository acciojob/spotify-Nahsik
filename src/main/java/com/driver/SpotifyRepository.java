package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository() {
        // To avoid hitting apis multiple times, initialize all the hashmaps here with
        // some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Album album = new Album(title);
        albums.add(album);
        Artist artist = new Artist(artistName);
        if (!artists.contains(artist)) {
            createArtist(artistName);
        }
        List<Album> albumList = artistAlbumMap.getOrDefault(artist, new ArrayList<>());
        albumList.add(album);
        artistAlbumMap.put(artist, albumList);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception {
        Album album = new Album(albumName);
        if (!albums.contains(album)) {
            throw new Exception("Album does not exist");
        }
        Song song = new Song(title, length);
        songs.add(song);
        List<Song> songList = albumSongMap.getOrDefault(album, new ArrayList<>());
        songList.add(song);
        albumSongMap.put(album, songList);
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Playlist playlist = new Playlist(title);
        List<Song> playListSongs = new ArrayList<>();
        songs.forEach(song -> {
            if (song.getLength() == length) {
                playListSongs.add(song);
            }
        });
        playlists.add(playlist);
        playlistSongMap.put(playlist, playListSongs);
        creatorPlaylistMap.put(getUser(mobile), playlist);
        List<User> listener = new ArrayList<>();
        listener.add(getUser(mobile));
        playlistListenerMap.put(playlist, listener);
        return playlist;
    }

    private User getUser(String mobile) {
        User user = new User();
        for (User eachUser : users) {
            if (Objects.equals(eachUser.getMobile(), mobile)) {
                return eachUser;
            }
        }
        return user;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {

        Playlist playlist = new Playlist(title);
        List<Song> playListsSong = new ArrayList<>();

        songTitles.forEach(eachSong -> {
            Song song = getSong(eachSong);
            if (song != null) {
                playListsSong.add(song);
            }
        });
        playlistSongMap.put(playlist, playListsSong);
        creatorPlaylistMap.put(getUser(mobile), playlist);
        playlists.add(playlist);

        List<User> listener = new ArrayList<>();
        listener.add(getUser(mobile));
        playlistListenerMap.put(playlist, listener);

        return playlist;
    }

    private Song getSong(String songTitle) {
        for (Song eachSong : songs) {
            if (Objects.equals(eachSong.getTitle(), songTitle)) {
                return eachSong;
            }
        }
        return null;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        Playlist playlist = new Playlist(playlistTitle);
        if (!playlists.contains(playlist)) {
            throw new Exception("play list not found");
        }
        User user = getUser(mobile);
        List<User> lisner = playlistListenerMap.getOrDefault(playlist, new ArrayList<>());
        if (!lisner.contains(user)) {
            lisner.add(user);
            playlistListenerMap.put(playlist, lisner);
        }
        return playlist;
    }


    /**
     * @param userName
     * @param mobile
     * @param songTitle
     * @return
     * @throws Exception
     */
    public Song likeSong(String userName, String mobile, String songTitle) throws Exception {
        User user = new User(userName, mobile);
        Song song = getSong(songTitle);
        if (!users.contains(user)) {
            throw new Exception("User does not exist");
        }
        if (!songs.contains(song)) {
            throw new Exception("Song does not exist");
        }
        if (!songLikeMap.getOrDefault(song, new ArrayList<>()).contains(user)) {
            List<User> list = songLikeMap.getOrDefault(song, new ArrayList<>());
            list.add(user);
            songLikeMap.put(song, list);
        }
        return song;
    }

    public String mostPopularArtist() {
        return "name";
    }

    public String mostPopularSong() {
        Song song = null;
        int maxLike = 0;
        for (Song eachSong : songLikeMap.keySet()) {
            int like = songLikeMap.get(eachSong).size();
            if (maxLike < like) {
                song = eachSong;
                maxLike = like;
            }
        }
        return null!=song ? song.getTitle() : null;
    }
}
