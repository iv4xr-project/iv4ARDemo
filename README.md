# iv4ARDemo

Demonstrating basic integration of iv4xr to target an ARCore application

# NOTES

* You need connect your real Adroid device to try AR features.

* Gradle complains about "Unable to make field private final java.lang.String java.io.File.path accessible etc".
This is likely because you use an incompatible JDK. Goto File > Project Structure. Set the JDK from
there. JDK 16 (or higher?) is not compatible. I use JDK 11.