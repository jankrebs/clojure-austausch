(ns clojure-austausch.task
  (:require
   [schema.core :as s]))

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
   :key-with-default-2 s/Str
   (s/optional-key :optional-key) s/Str
   })

;; convention-config -> stack-config
;; Hint: (assoc map key val) updates a key-value pair in the map.
