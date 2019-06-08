(ns corewars.controlpanel
  (:require [impi.core :as impi]
            [corewars.interpreter :as interpreter]
            [corewars.examples :as examples]))

(enable-console-print!)

(defn draw-machine
  [machine]
  (let [nx      100
        ny      80
        cw      10
        ch      10
        journal (into {} (mapv vector (reverse (take 10 (reverse (:journal machine)))) (range)))
        ptrs    (into {} (mapv vector (:ptrs machine) (range)))]
    (impi/mount
     :example-scene
     {:pixi/renderer {:pixi.renderer/size [1000 800]}
      :pixi/stage    {:impi/key                :performance
                      :pixi.object/type        :pixi.object.type/container
                      :pixi.container/children
                      [{:impi/key             :gfx
                        :pixi.object/type     :pixi.object.type/graphics
                        :pixi.graphics/shapes (concat (for [addr (range (count (:memory machine)))
                                                            :let [x (mod addr 100)
                                                                  y (int (/ addr 100))]]
                                                        (cond (not= 0 (get (:memory machine) addr))
                                                              {:impi/key            [x y]
                                                               :pixi.shape/position [(* cw x) (* ch y)]
                                                               :pixi.shape/type     :pixi.shape.type/rectangle
                                                               :pixi.shape/size     [9 9]
                                                               :pixi.shape/fill     {:pixi.fill/color (cond (ptrs addr)    0xffffff
                                                                                                            (journal addr) (+ (* (- 0xff (mod (journal addr) 0x100)) 0x000100)
                                                                                                                              (* (mod (journal addr) 0x100) 0x010000))
                                                                                                            :else          0x770000)
                                                                                     :pixi.fill/alpha 1}}

                                                              :else
                                                              {:impi/key            [x y]
                                                               :pixi.shape/position [(inc (* cw x)) (* ch y)]
                                                               :pixi.shape/type     :pixi.shape.type/rectangle
                                                               :pixi.shape/size     [9 9]
                                                               :pixi.shape/line
                                                               {:pixi.line/width 1
                                                                :pixi.line/color 0x22FF11
                                                                :pixi.line/alpha 0.5}})))}]}}
     (.getElementById js/document "app"))))



(defonce animated
  (let [machine    (atom examples/machine)]
    (reset! interpreter/*machine-store-hook* (fn [machine addr value]
                                               (update machine :journal conj addr)))

    
    (js/requestAnimationFrame
     (fn cb []
       (try
         (draw-machine (swap! machine #(interpreter/machine-step (assoc % :journal nil))))
         (js/requestAnimationFrame cb)
         (catch js/Error e
           (.log js/console e)))))))

