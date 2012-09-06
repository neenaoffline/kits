(ns kits.logging
  (:use
   kits.foundation)
  (:import
   java.io.IOException
   java.util.logging.Level
   java.util.logging.Logger))

(defmacro log [^Level level ^String msg ^Throwable e]
  `(let [l# (Logger/getAnonymousLogger)
         e# ~e
         level# ~level]
     (when (.isLoggable ^Logger l# ^Level level#)
       (try
         (let [msg# (str ~@msg)]
           (if-not e#
             (.log ^Logger l# ^Level level# msg#)
             (.log ^Logger l# ^Level level# msg# ^Throwable e#)))
         (catch IOException ioe#
           ;; Sad but we swallow the exception so that a log message does
           ;; not trigger an unpredictable exception when file system is
           ;; full.
           )))))

(defmacro debug [& msg]
  `(log Level/FINE ~msg nil))

(defmacro info [& msg]
  `(kits.logging/log Level/INFO ~msg nil))

(defmacro warn [& msg]
  `(log Level/WARNING ~msg nil))

(defmacro error [^Throwable exception & msg]
  `(log Level/SEVERE ~msg ~exception))

(defmacro with-logging [& body]
  `(try
     (do ~@body)
     (catch Exception ex#
       (error ex#))))
