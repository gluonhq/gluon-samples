#!/usr/bin/env bash

# Run after 
#     mvn gluonfx:sharedlib

arch=`uname -m`
case $(uname | tr '[:upper:]' '[:lower:]') in
  linux*)
    g++ sample/example.cpp -I target/gluonfx/x86_64-linux/gvm/HelloSharedLib -L target/gluonfx/x86_64-linux/gvm/HelloSharedLib -Wl,target/gluonfx/x86_64-linux/HelloSharedLib.so -o target/example
    ./target/example 1 2
    ;;
  darwin*)
    # Mac OS X platform
    case "$arch" in
        x86_64) arch="x86_64-darwin" ;;
        arm64) arch="aarch64-darwin" ;;
    esac
    g++ sample/example.cpp -I target/gluonfx/$arch/gvm/HelloSharedLib -L target/gluonfx/$arch/gvm/HelloSharedLib -Wl,target/gluonfx/$arch/gvm/HelloSharedLib/hello.helloSharedLib.dylib -o target/example
    ./target/example 1 2
    ;;
  *)
    # not supported yet
    ;;
esac
