(ns corewars.decoder-test
  (:require  [clojure.test :refer [deftest is]]
             [corewars.emitter :as emitter]
             [corewars.decoder :as decoder]
             [corewars.parser :as parser]
             [corewars.examples :as examples]))

(deftest assemble-disassemble-test
  (is (= '([:jmp nil [:relative 2]]	  
	   [:dat nil [:immediate 0]]
	   [:add [:immediate 4] [:relative -1]]
	   [:mov [:immediate 3] [:indirect -2]]
	   [:jmp nil [:relative -2]])
         (parser/parse examples/dwarf)))
  (is (= (parser/parse examples/dwarf)
         (decoder/disassemble (emitter/assemble (parser/parse examples/dwarf)))))
  (is (= "DAT #0"
         (decoder/field-string [:dat nil [:immediate 0]])))
  (is (= "MOV #3 @-2"
         (decoder/field-string [:mov [:immediate 3] [:indirect -2]])))
  (is (= "ADD #4 -1"
         (decoder/field-string [:add [:immediate 4] [:relative -1]]))))


