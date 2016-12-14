/**
 * Copyright (c) 2016, Gluon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of Gluon, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq

import java.util.Optional
import java.util.concurrent.Callable
import java.util.function.Consumer
import java.util.function.Supplier
import javafx.application.Platform
import javafx.beans.binding.{Bindings, ObjectBinding}
import javafx.beans.property._
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.ListChangeListener
import javafx.collections.ListChangeListener.Change
import javafx.event.{Event, EventHandler}
import javafx.scene.Node
import javafx.scene.control.ButtonType
import javafx.stage.Window
import javafx.util.Callback

import com.sun.javafx.stage.StageHelper

import scala.collection.JavaConversions._
import scala.language.{implicitConversions, reflectiveCalls}


package object sqlite {

    def using[A, B <: {def close() : Unit}](closeable: B)(f: B => A): A = {
        try {
            f(closeable)
        } finally {
            Option(closeable).foreach(_.close())
        }
    }


    def getFocusedWindow: Option[Window] = StageHelper.getStages.find(_.isFocused)

    // Java and JavaFX implicit conversions

    implicit def optional2option[T](o: Optional[T]): Option[T] = if (o.isPresent) Some(o.get) else None


    // function to Runnable
    implicit def func2Runnable[R](func: => R): Runnable = new Runnable {
        def run() {
            func
        }
    }

    // function to Callable
    implicit def func2Callable[T]( func: => T ): Callable[T] = new Callable[T] {
        override def call(): T = func
    }

    // function to Callback
    implicit def func2Callback[P, R](func: P => R): Callback[P, R] = new Callback[P, R] {
        def call(param: P) = func(param)
    }

    implicit def func2Callback[P, R](func: => R): Callback[P, R] = new Callback[P, R] {
        def call(param: P) = func
    }


    // function 2 EventHandler
    implicit def func2EvenHandler[P <: Event, R](func: P => R): EventHandler[P] = new EventHandler[P] {
        def handle(event: P) = func(event)
    }

    implicit def func2EvenHandler[P <: Event, R](func: => R): EventHandler[P] = new EventHandler[P] {
        def handle(event: P) = func
    }

    // function 2 Consumer
    implicit def func2Consumer[T]( func: T => Unit ): Consumer[T] = new Consumer[T]{
        def accept( t: T ): Unit = func(t)
    }

    implicit def func2Consumer[T]( func: => Unit ): Consumer[T] = new Consumer[T]{
        def accept( t: T ): Unit = func
    }

    // function 2 supplier
    implicit def func2Supplier[T]( func: => T ): Supplier[T] = new Supplier[T]{
        def get = func
    }


    // function to change listener
    implicit def func2ChangeListener3[T, R](func: (ObservableValue[_ <: T], T, T) => R): ChangeListener[T] = new ChangeListener[T] {
        def changed(o: ObservableValue[_ <: T], oldValue: T, newValue: T): Unit = func(o, oldValue, newValue)
    }

    implicit def func2ChangeListener2[T, R](func: (T, T) => R): ChangeListener[T] = new ChangeListener[T] {
        def changed(o: ObservableValue[_ <: T], oldValue: T, newValue: T): Unit = func(oldValue, newValue)
    }

    implicit def func2ChangeListener1[T, R](func: T => R): ChangeListener[T] = new ChangeListener[T] {
        def changed(o: ObservableValue[_ <: T], oldValue: T, newValue: T): Unit = func(newValue)
    }


    // function to invalidation listener
    implicit def func2InvalidationListener[T, R](func: Observable => Unit): InvalidationListener = new InvalidationListener {
        def invalidated(o: Observable): Unit = func(o)
    }

    implicit def func2InvalidationListener[T, R](func: => Unit): InvalidationListener = new InvalidationListener {
        def invalidated(o: Observable): Unit = func
    }

    // function to ListChangeListener
    implicit def func2ListChangeListener[T]( func: Change[_ <: T] => Unit ): ListChangeListener[T] = new ListChangeListener[T] {
        override def onChanged(c: Change[_ <: T]): Unit = func(c)
    }

    implicit def func2ListChangeListener[T]( func: => Unit ): ListChangeListener[T] = new ListChangeListener[T] {
        override def onChanged(c: Change[_ <: T]): Unit = func
    }


    implicit class BooleanBindings( property: BooleanProperty ) {

        def bind( dependencies: Observable*  )(func: => Boolean): Unit = {
            val binding = Bindings.createBooleanBinding( new java.lang.Boolean(func), dependencies: _* )
            property.bind(binding)
        }

    }

    implicit class IntegerBindings( property: IntegerProperty ) {

        def bind( dependencies: Observable*  )(func: => Integer): Unit = {
            val binding = Bindings.createIntegerBinding( new java.lang.Integer(func), dependencies: _* )
            property.bind(binding)
        }

    }

    implicit class LongBindings( property: LongProperty ) {

        def bind( dependencies: Observable*  )(func: => Long): Unit = {
            val binding = Bindings.createLongBinding( new java.lang.Long(func), dependencies: _* )
            property.bind(binding)
        }

    }


    implicit class FloatBindings( property: FloatProperty ) {

        def bind( dependencies: Observable*  )(func: => Float): Unit = {
            val binding = Bindings.createFloatBinding( new java.lang.Float(func), dependencies: _* )
            property.bind(binding)
        }

    }

    implicit class DoubleBindings( property: DoubleProperty ) {

        def bind( dependencies: Observable*  )(func: => Double): Unit = {
            val binding = Bindings.createDoubleBinding( new java.lang.Double(func), dependencies: _* )
            property.bind(binding)
        }

    }


    implicit class StringBindings( property: StringProperty ) {

        def bind( dependencies: Observable*  )(func: => String): Unit = {
            val binding = Bindings.createStringBinding( func, dependencies: _* )
            property.bind(binding)
        }

    }

    implicit class ObjectBindings[T]( property: Property[T] ) {

        def bind( dependencies: Observable*  )(func: => T): Unit = {
            val binding: ObjectBinding[T] = Bindings.createObjectBinding[T]( func, dependencies: _* )
            property.bind( binding )
        }

    }

    /**
     * Runs action on FX thread
      *
      * @param action Action to run on FX thread
     */
    def onFX(action: => Any): Unit =  if (Platform.isFxApplicationThread) action else Platform.runLater(action) // has to go after all implicits

    implicit class ButtonTypeImplicits( bt: ButtonType ) {
        def custom(name: String): ButtonType = {
            Option(name).map(new ButtonType(_, bt.getButtonData)).getOrElse(bt)
        }
    }

    implicit class NodeImplicits( node: Node ) {

        import java.lang.{Boolean => JBoolean}

        def setFlag( flagName: String, value: Boolean = true ): Unit = {
            node.getProperties.put(flagName, if (value) JBoolean.TRUE else JBoolean.FALSE)
        }

        def isFlag( flagName: String ): Boolean = {
            node.getProperties.getOrDefault(flagName, JBoolean.FALSE) match {
                case b: JBoolean => b.booleanValue
                case _ => false
            }
        }

    }

}