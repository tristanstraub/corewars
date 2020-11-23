(ns corewars.emitter)

(defn bit-shift-field
  ([shift-register bits value]
   (bit-or (bit-shift-left shift-register bits)
           (bit-and value (dec (bit-shift-left 1 bits))))))

(defn machine-emit
  [[mnemonic [op1-type op1-value :as op1] [op2-type op2-value :as op2]]]
  (-> 0
      (bit-shift-field 4 (case mnemonic
                           :dat 0
                           :mov 1
                           :add 2
                           :sub 3
                           :jmp 4
                           :jmz 5
                           :djz 6
                           :cmp 7))
      (bit-shift-field 2 (if op1
                           (case op1-type
                             :immediate 0
                             :relative  1
                             :indirect  2)
                           0))
      (bit-shift-field 2 (if op2
                           (case op2-type
                             :immediate 0
                             :relative  1
                             :indirect  2)
                           0))
      (bit-shift-field 12 (if op1
                            op1-value
                            0))
      (bit-shift-field 12 (if op2
                            op2-value
                            0))))

(defn assemble
  [instructions]
  (map machine-emit instructions))
