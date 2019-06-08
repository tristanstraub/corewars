(ns corewars.emulator
  (:require [corewars.emitter :as emitter]
            [corewars.parser :as parser]
            [corewars.examples :as examples]
            [corewars.decoder :as decoder]))

(def machine
  {:memory       (vec (emitter/pack (parser/parse examples/dwarf)))
   :ptr          0})
