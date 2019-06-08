(ns corewars.decoder)

(defn bit-unshift-field
  ([{:keys [instruction field]} bits]
   {:instruction (conj instruction (bit-and field (dec (bit-shift-left 1 bits))))
    :field       (bit-shift-right field bits)}))

(defn unpack
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

(defn decode
  [field]
  (let [[mnemonic op1-type op2-type op1-value op2-value] (unpack field)]
    [(case mnemonic
       0 :dat
       1 :mov
       2 :add
       3 :sub
       4 :jmp
       5 :jmz
       6 :djz
       7 :cmp)
     (when op1-type
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
