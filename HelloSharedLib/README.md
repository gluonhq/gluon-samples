
# HelloSharedLib

A simple Hello World application with Java 11+ and GraalVM that shows how to create a shared library.

## Quick Instructions

We use [GluonFX plugin](https://docs.gluonhq.com/) to build a native image for platforms including desktop, android and iOS.
Please follow the prerequisites as stated [here](https://docs.gluonhq.com/#_requirements).

### Desktop (Linux and macOS)

Build a native image as shared library using:

    mvn gluonfx:sharedlib

### Test the shared library

Compile and run the sample `sample/example.cpp` that makes use of the library:

    ./run.sh

The expected output is:

    Sum: 3
    Diff: -1
    Text: Hello from Java

### Android

Build a native image as shared library for Android using:

    mvn gluonfx:sharedlib -Pandroid

### Test the shared library

On a regular Android project that includes already C++ code, add the headers:

- target/gluonfx/$arch-$os/gvm/HelloSharedLib/graal_isolate.h
- target/gluonfx/$arch-$os/gvm/HelloSharedLib/hello.hellosharedlib.h

and the library:

- target/gluonfx/$arch-$os/HelloSharedLib.dylib

to the project.

In the Cpp file with the JNI implementations, import the header:
```
#include <hello.hellosharedlib.h>
```

and then add the calls to the shared methods, like:

```

static jboolean initialized;
static graal_isolatethread_t *thread;

extern "C" JNIEXPORT jdouble JNICALL
Java_com_gluonhq_hellosharedandroidworld_MainActivity_sum(JNIEnv *env, jobject activity, jdouble a, jdouble b) {
    if (!initialized) {
        if (graal_create_isolate(NULL, NULL, &thread) != 0) {
            fprintf(stderr, "graal_create_isolate error\n");
            return -1;
        }
        initialized = true;
    }
    return sharedSum(thread, a, b);
}
```
assuming the `MainActivity` class has the native method:
```
private native double sum(double a, double b);
```

and the library is loaded:
```
static {
    System.loadLibrary("HelloSharedLib");
}
```

Build, making sure the shared library is added to the APK, and deploy to your device and test.

The output of the given call should look like:

```
2022-07-20 21:06:32.348 22110-22110/com.gluonhq.hellosharedandroidworld V/MainActivity: Sum: 3.0
2022-07-20 21:06:32.348 22110-22110/com.gluonhq.hellosharedandroidworld V/MainActivity: Difference: -1.0
2022-07-20 21:06:32.348 22110-22110/com.gluonhq.hellosharedandroidworld V/MainActivity: Text: Hello from Java
```

### iOS

Build a native image as shared library for iOS using:

    mvn gluonfx:sharedlib -Pios

### Test the shared library

On a regular iOS project, add the headers:

- target/gluonfx/$arch-$os/gvm/HelloSharedLib/graal_isolate.h
- target/gluonfx/$arch-$os/gvm/HelloSharedLib/hello.hellosharedlib.h

and the library:

- target/gluonfx/$arch-$os/HelloSharedLib.dylib

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

    #import "hello.hellosharedlib.h"

Add some code that makes use of the shared methods to your controller, like:

```
-(void) runFromSharedLib:(UIButton*)sender
{
    graal_isolatethread_t *thread = NULL;
    if (graal_create_isolate(NULL, NULL, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        return;
    }
    
    double resultSum = sharedSum(thread, 1.0, 2.0);
    double resultDiff = sharedDiff(thread, 1.0, 2.0);
    char* resultText = sharedText(thread);

    NSLog(@"Shared sum: %.1f", resultSum);
    NSLog(@"Shared diff: %.1f", resultDiff);
    NSLog(@"Shared text: %s", resultText);
    
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
2022-07-20 19:59:18.762690+0200 HelloSharedLib[51489:9848288] Shared sum: 3.0
2022-07-20 19:59:18.762903+0200 HelloSharedLib[51489:9848288] Shared diff: -1.0
2022-07-20 19:59:18.763037+0200 HelloSharedLib[51489:9848288] Shared text: Hello from Java
```