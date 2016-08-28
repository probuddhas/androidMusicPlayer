# androidMusicPlayer
Another Simple Music player for Android. The player scans the phone's system memory and lists the files in a listView. The user selects the media and is presented with the Player activity for controlling media Playback.
##Features :
* [x] User can **Multitask**. Music keeps on playing in background.
* [x] **Seekbar** Support
* [x] Displays Elapsed/Remaining Time
* [x] **Revind & Fast Forward** Support
* [x] **Data Persistence**. After exiting, media starts from the same file on restarting.<br><br>

## Possible Modifications :
* [x] Currently the whole fileSystem is scanned eachtime for Building the listView for building the playlist. Hence takes a long time. Building the listView using a **CursorAdapter** can save time when the Playlist is opened each time. <br>i.e. Scan the fileSystem once -> Store data in an SQLite DB -> Extract data using CursorAdapter.
* [x] Adding **Shuffle/Repeat** Support.
* [x] Using a **Custom listView** for better playlist UI.
* [x] **Auto-Database update** for updating music DB each time a new file is added.
* [x] **Splash Screen**
* [x] **Notifications** 
* [x] **Responsive Design** to support Multiple Screen Sizes <br><br>

## Video Explanation : <br>
<img src='http://i.imgur.com/jIZAtU1.gif' title='Video Explanation' width='' alt='Video Explanation' />
