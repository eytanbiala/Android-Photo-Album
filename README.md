#CS213 Android Photo Album Project

####By Eytan Biala

#####Features:

- All data is persistent, the app can be closed or paused at any time.
- You can create as many albums as you'd like. Any album can be deleted or renamed when you are in the album view. If an album is deleted, its photos are deleted if they don't exist in any other album.
- Add a photo to an album from the Android Gallery or by using the Camera to take a picture.
- Tags can be added to any photo from the full image activity. Only "Person" and "Location" tag types are allowed.
- A photo can be moved to a different album from the full image activity only if there is more than one album.
- Slideshow can be done from the full image activity, advancing to the next or previous photo in the album.
- Search can be done from the full image activity or the main activity. Searching allows for completion, and is done based on the tag value. If there are results, the matching photo can be displayed by tapping on the given result.

#####Notes
- To add photos to an album, you first need to have some photos in the Android Gallery on the device. On Jelly Bean, from the "Choose Picture" intent, you can choose the menu option **Capture Picture**, take pictures using the Camera, and then return to the Gallery to choose a picture.
- Compatible with Android API 14 (4.0) and later.


