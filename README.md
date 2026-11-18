# PrintCompare Web — Slot-27

Version 0.3.0 is a standalone browser application. Download index.html and open it in a modern browser. No installation, server, external libraries or internet connection are needed after downloading. For a public website, enable GitHub Pages in repository Settings > Pages > Deploy from a branch > main / (root). Hosting has not been verified.

Choose two full or partial fingerprint images. Tap Crop image, drag around the print using a finger or mouse, and tap Apply crop. Reset crop restores the original. Enter the true capture DPI if known, then press Compare fingerprints.

Results are labeled Possible Match, No Match Found, or Inconclusive. These are experimental geometric similarity labels using uncalibrated heuristics, not forensic decisions. Possible Match requires at least 14 candidate correspondences and 72% coverage of the smaller detected feature set. No Match Found requires both sets to have at least 14 features and coverage at or below 28%. Other results are inconclusive. A low result may reflect non-overlapping partial impressions rather than different fingers. Green markers show tentative correspondences; orange markers show other detected ridge features. The application searches rotation and translation after local adaptive thresholding, thinning, and detecting candidate ridge endings and branches.

The feature coverage percentage is not an identity probability. The prototype has not been validated with real fingerprint datasets. Unknown DPI, distortion, poor contrast, different scales, false ridge features and non-overlapping fragments can all make the result unreliable. Only compare images you have permission to use. Do not use comparisons for forensic identification or decisions affecting people.

Images are processed in browser memory, not uploaded or retained by the app. This is a public repository; never commit fingerprint images. Browser or operating system histories and caches remain outside application control.

## Earlier desktop prototype
The Java SourceAFIS version 0.1.0 remains available in the source tree. It is separate from the browser algorithm. Build it with JDK 17+ and Maven: mvn clean verify, then java -jar target/printcompare-0.1.0.jar.
