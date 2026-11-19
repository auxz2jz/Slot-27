# PrintCompare Web — Slot-27

Version 0.6.1 is a standalone browser application. Download index.html and open it in a modern browser. No installation, server, external libraries or internet connection are needed after downloading. For a public website, enable GitHub Pages in repository Settings > Pages > Deploy from a branch > main / (root). Hosting has not been verified.

Choose two full or partial fingerprint images. Tap Crop image, drag around the print using a finger or mouse, and tap Apply crop. Reset crop restores the original. Enter the true capture DPI if known, then press Compare fingerprints.

Results are labeled Possible Match, No Match Found, or Inconclusive. These are experimental geometric similarity labels using uncalibrated heuristics, not forensic decisions. Possible Match requires at least 14 candidate correspondences and 72% coverage of the smaller detected feature set. No Match Found requires both sets to have at least 14 features and coverage at or below 28%. Other results are inconclusive. A low result may reflect non-overlapping partial impressions rather than different fingers. Green markers show tentative correspondences; orange markers show other detected ridge features. The application searches uniform scale (0.25×–4× relative to entered DPI), rotation and translation after local adaptive thresholding, thinning, and detecting candidate ridge endings and branches.

The feature coverage percentage is not an identity probability. The prototype has not been validated with real fingerprint datasets. Unknown DPI, distortion, poor contrast, different scales, false ridge features and non-overlapping fragments can all make the result unreliable. Only compare images you have permission to use. Do not use comparisons for forensic identification or decisions affecting people.

Images are processed in browser memory, not uploaded or retained by the app. This is a public repository; never commit fingerprint images. Browser or operating system histories and caches remain outside application control.

## Earlier desktop prototype
The Java SourceAFIS version 0.1.0 remains available in the source tree. It is separate from the browser algorithm. Build it with JDK 17+ and Maven: mvn clean verify, then java -jar target/printcompare-0.1.0.jar.

## Automatic size correction
Each cropped image is independently normalized to a processing canvas. The comparison searches for uniformly scaled ridge feature arrangements rather than forcing both images to the same width or height. The result includes the estimated relative size correction. Disable the checkbox to use only the entered DPI relationship. JavaScript syntax and synthetic feature-point tests for differently scaled, rotated, translated partial arrangements passed; these are not real-print accuracy tests. Larger searches may take longer on phones.

## Duplicate-image detection and manual overlay (0.5.0)
Before feature extraction, the selected images/crops are checked for exact decoded pixel equality, using 256-pixel tiles. Exact equality returns MATCH — IDENTICAL IMAGE even when there are too few ridge features. A conservative normalized-pixel correlation check also looks for near-identical images at different resolutions. Neither establishes identity from independent impressions. The manual overlay provides separate opacity, size, angle, and X/Y controls, touch/mouse dragging, colored ridges, visibility toggles, view zoom, reset and optional automatic alignment. Manual adjustments are visual only. Syntax, synthetic pixel-statistics checks and mock-canvas duplicate checks passed; browser interaction and real fingerprint accuracy are not yet validated.

## V0.6.1 numbered landmark alignment
Built on the existing GitHub V0.5.0 page; crop, duplicate checks, automatic feature comparison and overlay are preserved. The new third section uses the current crops. Each photo supports 50%–12,800% fitted-view zoom, pinch gestures, wheel, zoom buttons, independent view rotation, drag-to-pan, numbered pin placement/replacement, undo and clearing. Pins are held in image coordinates. A least-squares uniform similarity transform aligns A to B using paired numbers and reports RMS and per-point residual errors. It never stretches axes independently. Two pairs alone cannot validate correspondence. Applying alignment sends precise layer transforms to the existing overlay. Cropping or replacing an image clears its pins. Syntax, mathematical transform and mocked pinch/tap coordinate tests passed; physical browser touchscreen testing and biometric accuracy remain unverified.
