# PrintCompare — Slot-27

A local desktop application for comparing two fingerprint images, either full or partial, using SourceAFIS. No identity database, no network uploads, and no print files stored by the application.

## Run on Windows, Linux, or macOS
Install JDK 17+ and Apache Maven 3.9+. Clone this repository, then run:
```sh
mvn clean verify
java -jar target/printcompare-0.1.0.jar
```
On Windows you can also use run-windows.cmd after installing Java and Maven.

## Workflow
1. Select the first fingerprint image and the second fingerprint image (PNG, JPEG or BMP).
2. Enter the true capture resolution in DPI for each. 500 DPI is a common scanner setting, not a universal value. Leave the uncertainty checkbox checked if DPI is assumed.
3. Crop surrounding material using an image editor before loading, but preserve the original pixel scale. Use the optional inversion checkbox for light ridges on a dark background.
4. Compare. The program shows the engine score and a cautious interpretation.
5. Clear to release application references to the images.

The default reference threshold is 40, from the SourceAFIS documentation. A score is not a percentage or a probability. Above the threshold means only that the algorithm found similarity; a low score does not rule out the same finger, especially with partial, distorted, low-quality, or differently scaled impressions. Non-overlapping fragments may be impossible to compare. The app is experimental, not validated for forensic identification, legal evidence, access control, or decisions affecting people. Use prints only with proper permission. Never upload real prints to this public repository.

The interface processes images locally in memory. The operating system may still retain file-picker history, thumbnails, swap, or crash information. No guarantee of secure erasure.

Engine documentation: https://sourceafis.machinezoo.com/java

## Development
- Version 0.1.0: two-image comparison, previews, per-image DPI, polarity control, input validation, asynchronous matching, cautious score reporting.
- Future: interactive cropping, optional minutiae correspondence visualization, test with consented or licensed datasets, accuracy and partial-print validation.
