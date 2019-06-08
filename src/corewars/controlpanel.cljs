(ns corewars.controlpanel
  (:require [impi.core :as impi]
            [corewars.interpreter :as interpreter]))

(enable-console-print!)

(defn draw-machine
  [machine]
  (let [nx 100
        ny 80
        cw 10
        ch 10]
    (impi/mount
     :example-scene
     {:pixi/renderer {:pixi.renderer/size [1000 800]}
      :pixi/stage    {:impi/key                :performance
                      :pixi.object/type        :pixi.object.type/container
                      :pixi.container/children
                      [{:impi/key             :gfx
                        :pixi.object/type     :pixi.object.type/graphics
                        :pixi.graphics/shapes (for [i (range (count (:memory machine)))
                                                    :let [x (mod i 100)
                                                          y (int (/ i 100))]]
                                                (if (not= 0 (get-in machine [:memory i]))
                                                  {:impi/key            [x y]
                                                   :pixi.shape/position [(* cw x) (* ch y)]
                                                   :pixi.shape/type     :pixi.shape.type/rectangle
                                                   :pixi.shape/size     [9 9]
                                                   :pixi.shape/fill     {:pixi.fill/color 0xff0000
                                                                         :pixi.fill/alpha 1}}
                                                  {:impi/key            [x y]
                                                   :pixi.shape/position [(inc (* cw x)) (* ch y)]
                                                   :pixi.shape/type     :pixi.shape.type/rectangle
                                                   :pixi.shape/size     [9 9]
                                                   :pixi.shape/line
                                                   {:pixi.line/width 1
                                                    :pixi.line/color 0x22FF11
                                                    :pixi.line/alpha 0.5}}))}]}}
     (.getElementById js/document "app"))))

(defonce animated
  (let [machine (atom interpreter/machine)]
    (js/requestAnimationFrame
     (fn cb []
       (draw-machine (swap! machine #(last (take 100 (iterate interpreter/machine-step %)))))
       (js/requestAnimationFrame cb)))))

