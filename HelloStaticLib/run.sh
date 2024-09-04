#!/usr/bin/env bash

# Run after 
#     mvn gluonfx:staticlib

arch=`uname -m`
case $(uname | tr '[:upper:]' '[:lower:]') in
  linux*)
    arch="x86_64-linux"
    g++ -c sample/example.c -I target/gluonfx/$arch/gvm/HelloStaticLib -o target/example.o
    g++ target/example.o -Ltarget/gluonfx/$arch/gvm -lHelloStaticLib -lvmone -lz -o target/gluonfx/$arch/HelloExample
    ./target/gluonfx/$arch/HelloExample 1 2
    ;;
  darwin*)
    # Mac OS X platform
    case "$arch" in
        x86_64) arch="x86_64-darwin" arch2="darwin-x86_64";;
        arm64) arch="aarch64-darwin" arch2="darwin-aarch64";;
    esac

    clang -c sample/example.c -I target/gluonfx/$arch/gvm/HelloStaticLib -o target/example.o
    clang target/example.o -Ltarget/gluonfx/$arch/gvm -lHelloStaticLib -lvmone -framework AppKit -lz -o target/gluonfx/$arch/HelloExample
    ./target/gluonfx/$arch/HelloExample 1 2
    ;;
  *)
    # not supported yet
    ;;
esac
