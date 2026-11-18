# PrintCompare Web — Slot-27

Version 0.2.0 is a standalone browser application. Download index.html and open it in a modern browser. No installation, server, external libraries or internet connection are needed after downloading. For a public website, enable GitHub Pages in repository Settings > Pages > Deploy from a branch > main / (root). Hosting has not been verified.

Choose two full or partial fingerprint images, enter the true capture DPI if known, and press Compare fingerprints. Green markers show tentative correspondences; orange markers show other detected ridge features. The application searches rotation and translation after local adaptive thresholding, thinning, and detecting candidate ridge endings and branches.

The feature coverage percentage is not an identity probability. The prototype has not been validated with real fingerprint datasets. Unknown DPI, distortion, poor contrast, different scales, false ridge features and non-overlapping fragments can all make the result unreliable. Only compare images you have permission to use. Do not use comparisons for forensic identification or decisions affecting people.

Images are processed in browser memory, not uploaded or retained by the app. This is a public repository; never commit fingerprint images. Browser or operating system histories and caches remain outside application control.

## Earlier desktop prototype
The Java SourceAFIS version 0.1.0 remains available in the source tree. It is separate from the browser algorithm. Build it with JDK 17+ and Maven: mvn clean verify, then java -jar target/printcompare-0.1.0.jar.
