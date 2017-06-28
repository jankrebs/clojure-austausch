(ns clojure-austausch.core
  (:gen-class)
  (:require
   [schema.core :as s]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                                        ; Code examples and threading macros.                                                                       ;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def convention-config
  {:required-key s/Str
   (s/optional-key :key-with-default) s/Str
   (s/optional-key :key-with-default-2) s/Str
   (s/optional-key :optional-key) s/Str})

(def example-convention-config
  {:required-key "my-required-key"})

(def stack-config
  {:required-key s/Str
   :key-with-default s/Str
   (s/optional-key :optional-key) s/Str
   })

;; convention-config -> stack-config
(defn create-stack-config-1
  "This happens when one spends to much time coding in java..."
  [convention-config]
  (if (:key-with-default convention-config)
    convention-config
    (assoc convention-config :key-with-default "my-default"))
  (if (:key-with-default-2 convention-config)
    convention-config
    (assoc convention-config :key-with-default-2 "my-default-2")))

(defn create-stack-config-2
  "Readibility can suffer because of nesting functions."
  [convention-config]
  (assoc
   (assoc convention-config :key-with-default "my-default")
   :key-with-default-2 "my-default-2"))

(defn create-stack-config-3 
  "With thread-first macro, we can avoid nesting functions and thus, increase readibility."
  [convention-config]
  (-> convention-config
      (assoc ,,, :key-with-default) (or (:key-with-default convention-config) "my-default")
      (assoc ,,, :key-with-default) (or (:key-with-default-2 convention-config) "my-default-2")))

;; Threre are more threading-macros, but the basic concept remains the same so we can omit them.
;; For further reading on threading-macros: https://clojure.org/guides/threading_macros

;; Discussion do you find threading macros useful? Should we use them more often?
;; Do you find them more readable/easier to understand the code with them or nested functions?



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                                        ;       Visitor and multimethods      ;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Visitor pattern
;; https://en.wikipedia.org/wiki/Visitor_pattern

;; defmulti and defmethods are very powerful, basic structure:

;; (defmulti name dispatch-function)
;; (defmethod name dispatch-value/type/class/...)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                                        ; Lets create a dispatch function for maps ;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def multi-convention-config
  {:type "static-webserver"})
(def multi-convention-config-2
  {:type "webserver"})

(defn map-type-dispatcher
  "Dispatches a convention-config to its type"
  [convention-config]
  (:type convention-config))

(defn create-static-webserver-stack-config
  "Create the stack-static-webserver config."
  [convention-config]
  (assoc convention-config :default "static-webserver-default"))

(defn create-webserver-stack-config
  "Create the stack-webserver config."
  [convention-config]
  (assoc convention-config :default "websever-default"))

(defmulti create-stack-config map-type-dispatcher)
(defmethod create-stack-config "static-webserver"
  [convention-config]
  (create-static-webserver-stack-config convention-config))
(defmethod create-stack-config "webserver"
  [convention-config]
  (create-webserver-stack-config convention-config))

;; This simple system is extremely powerful. One way to understand the relationship between Clojure
;; multimethods and traditional Java-style single dispatch is that single dispatch is like a Clojure
;; multimethod whose dispatch function calls getClass on the first argument, and whose methods are
;; associated with those classes. Clojure multimethods are not hard-wired to class/type, they can be
;; based on any attribute of the arguments, on multiple arguments, can do validation of arguments and
;; route to error-handling methods etc.

(defn multi-type-dispatcher
  ""
  [x y]
  (vector (class x) (class y)))

(defmulti type-dispatch multi-type-dispatcher)
(defmethod type-dispatch [java.lang.String java.lang.String]
  [x y]
  "arg is string string")

(defmethod type-dispatch [java.lang.Long java.lang.Long]
  [x y]
  "arg is Long Long")

;;Discussion: What do you think of multimethods? Are they useful? Are they useful for us and our codebase?
;; Where do we use them already? Why do we use them?

