# Technical-Task-1

Develop an app which downloads large images (>10MB) from a limited bandwidth (10mbps) server with pagination into a scrollable list.

1st round - Initial Task Objectives (Commit on Sep 26, 2017):
 - Use only standard Java and Android SDK tools;
 - Not implementing third party libraries is mandatory;
 - Complying with OOP principles;
 - All images regardless of orientation should have aspect ratio of 3:2;
 - Each image should have a text field below with its name;
 - Shown images must be loaded from cache;
 - Downloading/loading processes should not block UI;
 - All possible errors should be handled;
 - App must be orientation-change-resistant;
 - Images should be obtained from RESTful API server via. POST/GET requests.
 
2nd round (Commit on Sep 28, 2017):
 - Rewrite all code with using Retrofit2 and Universal Image Loader third party libraries.

3rd, final round (Commit on Oct 03, 2017)
 - Refactor all UI to dynamic Fragments which attach/detach to the Activity;
 - On item click, image should open in a full-screen Fragment;
 - Implement smart-zoom (possible to use third-pary library).
