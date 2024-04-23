(ns unzipme.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as string])
  (:import [java.lang ProcessBuilder]
           [java.nio.file Files]
           [java.nio.file.attribute PosixFilePermission PosixFilePermissions]
           [java.util Arrays EnumSet]))

(defn os-name [] 
  (let [name (string/lower-case (System/getProperty "os.name"))]
        (if (string/starts-with? name "win") "windows" name)))

(def is-win? (= (os-name) "windows"))

(defn os-arch []
  (let [os_arch (System/getProperty "os.arch")]
    (cond
      (= os_arch "amd64") "x86_64"
      :else os_arch)))

(defn resource-zip-file-path []
  (str "hello/hello-" (os-name) "-" (os-arch) ".zip"))

(defn binary-path-in-zip-file []
  (str (os-name) "-" (os-arch) "/hello_args"
       (if is-win? ".exe" "")))

(defn copy-resource-to-file [rsrc-path output-file-path is_executable]
  (let
   [in (io/input-stream (io/resource rsrc-path))
    out-file (io/file output-file-path)
    out (io/output-stream out-file)]
    (io/copy in out)
    (.close in)
    (.setExecutable out-file is_executable false)  ;; Set executable bit, and not just for owner (false).
    (.close out)
    output-file-path))

(defn unzip [resource-zip-file temp-dir]
  (with-open [zis (java.util.zip.ZipInputStream. (io/input-stream resource-zip-file))]
    (loop [entry (.getNextEntry zis)]
      (when entry
        (let [file (io/file temp-dir (.getName entry))]
          (when-not (.isDirectory entry)
            (io/make-parents file)
            (io/copy zis file))
          (recur (.getNextEntry zis)))))))

(defn make-executable [file]
  (let [perms (PosixFilePermissions/fromString "rwxr-xr-x")]
    (Files/setPosixFilePermissions (.toPath file) perms)))

(defn execute-binary-via-pb [binary-path & args]
  (println (str "Running '" binary-path "'. [pb]"))
  ;; (println (into [binary-path] args))
  (let [commands (into-array String (cons binary-path (flatten (remove nil? args))))
        pb (ProcessBuilder. (Arrays/asList commands))]
    (.inheritIO pb)
    (let [process (.start pb)]
      (.waitFor process)
      (println "Done. [pb]"))))

;; For some reason - presumably waiting on subprocess output? - this takes 60 seconds to finish.
(defn execute-binary-via-sh [binary-path & args]
  (println (str "Running '" binary-path "'. [sh]"))
  (let [result (apply sh binary-path (remove nil? (flatten args)))]
    (if result
      (println (:out result))
      (println (str "Running of '" binary-path "' failed.")))
    (println "Done. [sh]")))
;;  (let [pb (ProcessBuilder. (map str binary-io-file))]
;;    (.inheritIO pb)
;;    (.start pb)))

(def no-file-attributes (into-array java.nio.file.attribute.FileAttribute []))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println (str "Running '" (binary-path-in-zip-file) "' from resource '" (resource-zip-file-path) "'..."))
  (let [resource-url (io/resource (resource-zip-file-path))
        temp-dir (java.nio.file.Files/createTempDirectory "unzipme-" no-file-attributes)
        temp-dir-as-file (java.io.File. (.toUri temp-dir))
        temp-dir-binary-path (str temp-dir "/" (binary-path-in-zip-file))
        _ (println (str "temp-dir='" temp-dir "'"))
        ;; _ (copy-resource-to-file resource-zip-file-path (str temp-dir "/temp-hello.zip") false)
        _ (unzip resource-url temp-dir-as-file)
        binary-io-file (io/file temp-dir-as-file (binary-path-in-zip-file))]
    (if (not is-win?) (make-executable binary-io-file))
    (execute-binary-via-pb temp-dir-binary-path args)))
