
# HelloStaticLib

A simple Hello World application with Java 21+ and GraalVM that shows how to create a static library.

## Quick Instructions

We use [GluonFX plugin](https://docs.gluonhq.com/) to build a native image for platforms including desktop, android and iOS.
Please follow the prerequisites as stated [here](https://docs.gluonhq.com/#_requirements).

### Desktop (Linux and macOS)

Build a native image as static library using:

    mvn gluonfx:staticlib

### Test the static library (macOS, Linux)

Compile and run the sample `sample/example.cpp` that makes use of the library:

    ./run.sh

The expected output is:

    Sum: 3.0
    Diff: -1.0
    Text: Hello from Java

### Android

Build a native image as static library for Android using:

    mvn gluonfx:staticlib -Pandroid

### Test the static library

On a regular Android project that includes already C++ code, add the headers:

- target/gluonfx/aarch64-android/gvm/HelloStaticLib/graal_isolate.h
- target/gluonfx/aarch64-android/gvm/HelloStaticLib/hello.hellostaticlib.h

and the libraries:

- target/gluonfx/aarch64-android/gvm/HelloStaticLib.a
- target/gluonfx/aarch64-android/gvm/vmoneb.a

to the project.

In the Cpp file with the JNI implementations, import the header:
```
#include <hello.hellostaticlib.h>
```

and then add the calls to the static methods, like:

```

static jboolean initialized;
static graal_isolatethread_t *thread;

extern "C" JNIEXPORT jdouble JNICALL
Java_com_gluonhq_hellostaticandroidworld_MainActivity_sum(JNIEnv *env, jobject activity, jdouble a, jdouble b) {
    if (!initialized) {
        if (graal_create_isolate(NULL, NULL, &thread) != 0) {
            fprintf(stderr, "graal_create_isolate error\n");
            return -1;
        }
        initialized = true;
    }
    return staticSum(thread, a, b);
}
```
assuming the `MainActivity` class has the native method:
```
private native double sum(double a, double b);
```

and the library is loaded:
```
static {
    System.loadLibrary("HelloStaticLib");
}
```

Build, making sure the static library is added to the APK, and deploy to your device and test.

The output of the given call should look like:

```
2022-07-20 21:06:32.348 22110-22110/com.gluonhq.hellostaticandroidworld V/MainActivity: Sum: 3.0
2022-07-20 21:06:32.348 22110-22110/com.gluonhq.hellostaticandroidworld V/MainActivity: Difference: -1.0
2022-07-20 21:06:32.348 22110-22110/com.gluonhq.hellostaticandroidworld V/MainActivity: Text: Hello from Java
```

### iOS

Build a native image as static library for iOS using:

    mvn gluonfx:staticlib -Pios

### Test the static library

On a regular iOS project, add the headers:

- target/gluonfx/arm64-ios/gvm/HelloStaticLib/graal_isolate.h
- target/gluonfx/arm64-ios/gvm/HelloStaticLib/hello.hellostaticlib.h

and the libraries:

- target/gluonfx/arm64-ios/gvm/HelloStaticLib.a
- target/gluonfx/arm64-ios/gvm/vmoneb.a

to the project. 

In graal_isolate.h, change:
```
#include <graal_isolate.h>
```

into

```
#include "graal_isolate.h"
```

Add the required import to the project, for instance to `ViewController.h`:

    #import "hello.hellostaticlib.h"

Add some code that makes use of the static methods to your controller, like:

```
-(void) runFromStaticLib:(UIButton*)sender
{
    graal_isolatethread_t *thread = NULL;
    if (graal_create_isolate(NULL, NULL, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        return;
    }
    
    double resultSum = staticSum(thread, 1.0, 2.0);
    double resultDiff = staticDiff(thread, 1.0, 2.0);
    char* resultText = staticText(thread);

    NSLog(@"Static sum: %.1f", resultSum);
    NSLog(@"Static diff: %.1f", resultDiff);
    NSLog(@"Static text: %s", resultText);
    
    if (graal_detach_thread(thread) != 0) {
         fprintf(stderr, "graal_detach_thread error\n");
         return;
    }
}
```

Before building the project, disable bitcode, link the binary with the library, and copy it to frameworks.

Build, and deploy to your device and test.

The output of the given call should look like:

```
2022-07-20 19:59:18.762690+0200 HelloStaticLib[51489:9848288] Static sum: 3.0
2022-07-20 19:59:18.762903+0200 HelloStaticLib[51489:9848288] Static diff: -1.0
2022-07-20 19:59:18.763037+0200 HelloStaticLib[51489:9848288] Static text: Hello from Java
```