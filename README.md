# androidMusicPlayer
Another Simple Music player for Android. The player scans the phone's system memory and lists the files in a listView. The user selects the media and is presented with the Player activity for controlling media Playback.
##Features :
* [x] User can **Multitask**. Music keeps on playing in background.
* [x] **Seekbar** Support
* [x] Displays Elapsed/Remaining Time
* [x] **Revind & Fast Forward** Support
* [x] **Data Persistence**. After exiting, media starts from the same file on restarting.
* 

## Possible Modifications :
* [x] Building the listView using a CursorAdapter can save time when the Playlist is opened. <br>i.e. Scan the fileSystem once -> Store in a SQLite DB --> Extract data using CursorAdapter.
