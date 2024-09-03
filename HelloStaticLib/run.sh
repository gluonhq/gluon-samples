#!/usr/bin/env bash

# Run after 
#     mvn gluonfx:staticlib

arch=`uname -m`
case $(uname | tr '[:upper:]' '[:lower:]') in
  linux*)
    g++ sample/example.cpp -I target/gluonfx/x86_64-linux/gvm/HelloStaticLib -L target/gluonfx/x86_64-linux/gvm/HelloStaticLib -Wl,target/gluonfx/x86_64-linux/HelloStaticLib.so -o target/example
    ./target/example 1 2
    ;;
  darwin*)
    # Mac OS X platform
    case "$arch" in
        x86_64) arch="x86_64-darwin" ;;
        arm64) arch="aarch64-darwin" ;;
    esac

    clang -c sample/example.c -I target/gluonfx/$arch/gvm/HelloStaticLib -o target/example.o
    clang target/example.o -Ltarget/gluonfx/$arch/gvm/HelloStaticLib -L/Users/JosePereda/Downloads/iostmp/lib -Ltarget/gluonfx/$arch/gvm -lHelloStaticLib -lvmone -framework AppKit -lz -o target/gluonfx/$arch/HelloExample
    ./target/gluonfx/$arch/HelloExample 1 2
    ;;
  *)
    # not supported yet
    ;;
esac
