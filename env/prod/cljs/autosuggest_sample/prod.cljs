(ns autosuggest-sample.prod
  (:require [autosuggest-sample.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
