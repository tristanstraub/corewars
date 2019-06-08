(ns corewars.decoder
  (:require [clojure.string :as str]))

(defn decode-sign
  [bits value]
  (if (= 1 (bit-and 1 (bit-shift-right value (dec bits))))
    (- (inc (bit-xor value (bit-and -1 (dec (bit-shift-left 1 bits))))))
    value))

(defn bit-unshift-field
  ([{:keys [instruction field]} bits]
   {:instruction (conj instruction (decode-sign 12 (bit-and field (dec (bit-shift-left 1 bits)))))
    :field       (unsigned-bit-shift-right field bits)}))

(defn unpack-1
  [field]
  (-> {:instruction []
       :field       field}
      (bit-unshift-field 12)
      (bit-unshift-field 12)
      (bit-unshift-field 2)
      (bit-unshift-field 2)
      (bit-unshift-field 4)
      :instruction
      reverse
      vec))

(defn disassemble-mnemonic
  [mnemonic]
  (case mnemonic
    0 :dat
    1 :mov
    2 :add
    3 :sub
    4 :jmp
    5 :jmz
    6 :djz
    7 :cmp))

(defn disassemble-1
  [field]
  (let [[mnemonic op1-type op2-type op1-value op2-value] (unpack-1 field)]
    [(disassemble-mnemonic mnemonic)
     (when (and op1-type
                (not (#{:jmp :dat} (disassemble-mnemonic mnemonic))))
       [(case op1-type
          0 :immediate
          1 :relative
          2 :indirect
          nil nil)
        op1-value])
     (when op2-type
       [(case op2-type
          0 :immediate
          1 :relative
          2 :indirect
          nil nil)
        op2-value])]))

(defn disassemble
  [fields]
  (map disassemble-1 fields))

(defn field-string
  [[mnemonic [op1-type op1-value :as op1] [op2-type op2-value :as op2]]]
  (str (str/upper-case (name mnemonic))
       (when op1
         (str " " (case op1-type
                    :immediate "#"
                    :relative  ""
                    :indirect  "@")
              op1-value))
       (when op2
         (str " " (case op2-type
                    :immediate "#"
                    :relative  ""
                    :indirect  "@")
              op2-value))))


