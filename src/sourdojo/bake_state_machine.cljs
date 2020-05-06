(ns sourdojo.bake-state-machine)

(def states
  {:origin {:start :new}
   :new {:begin-bake :mix}
   :mix {:begin-autolyse :autolysing
         :add-starter-begin-bulk :bulk-fermentation}
   :autolysing {:begin-bulk :bulk-fermentation}
   :bulk-fermentation {:stretch-and-fold :bulk-fermentation
                       :initial-shaping :bench-rest}
   :bench-rest {:final-shaping :ready-for-proving}
   :ready-for-proving {:begin-proving :proving}
   :proving {:bake :baking}
   :baking {:finish :finished}})

(defn translate
  [sym]
  (sym {:start "START"
        :new "Ready to bake"
        :begin-bake "🥣 Mix"
        :mix "Mix"
        :stretch-and-fold "💪 Stretch and fold"
        :begin-autolyse "🕐 Begin autolyse"
        :autolysing "Autolyse"
        :add-starter-begin-bulk "🚀 Add starter to begin bulk fermentation"
        :begin-bulk "🚀 Begin bulk fermentation"
        :bulk-fermentation "Bulk fermentation"
        :initial-shaping "👐 Initial shaping"
        :bench-rest "Shape & bench rest"
        :final-shaping "💅 Final shaping"
        :ready-for-proving "Ready for proving"
        :begin-proving "🌙 Begin proving"
        :proving "Proving"
        :bake "♨️ Bake"
        :baking "Baking"
        :finish "🥖 Finish!"
        :finished "Bake complete!"}))

(defn transitions-from
  [starting-state]
  (map first (starting-state states)))

(defn transition
  [state action]
  (get-in states [state action]))
