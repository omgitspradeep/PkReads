# PkReads
A simple PDF and EPUB reader application for Android.


# Used Dependencies

* In build.gradel(:app)
```
dependencies {
    ...
    implementation 'com.github.barteksc:android-pdf-viewer:3.0.0-beta.5'
    implementation "com.folioreader:folioreader:0.5.4"
}
```

* In build.gradel(PkReads)
```
allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" }
    }
    
```


# Features

* Lists all the pdf( with page number) and epub files of your device.
* Clicking a file from list displays it's content.
* You can Change (Left to right ) or (Top to Bottom) Reading mode just by pressing any volume UP or DOWN hardware keys ( only PDF).
* Lists your last read book in top of all files.
* Remembers the page number no. when you close the book and directly opens that page next time you click that same book from list.
* Supports both landscape and portrait screen orientation.
* No bells attached. Code is easy to understand.
