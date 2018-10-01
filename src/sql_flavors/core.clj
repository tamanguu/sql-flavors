(ns sql-flavors.core
  (:require [korma.db :as kd :refer [defdb postgres]]
            [korma.core :as k]
            [honeysql.core :as h]
            [honeysql.helpers :refer :all :as hp]
            [clojure.java.jdbc :as j]))

;
; KORMA
;

; Define korma database connection
(defdb db (postgres {:db       "contactsusers"
                     :user     "admin"
                     :password "admin"}))

; Name the entities needed
(k/defentity registereduser)
(k/defentity contacts)
(k/defentity items)

; Get a uuid for user with email-address
(defn user-email->uuid
  [email-address]
  (->> (k/select registereduser
                 (k/fields :userid)
                 (k/where {:useremail email-address}))
       (first)
       :userid))

; Select contacts with a priority for user with email-address
(defn contacts-with-priority
  [email-address]
  (->> (k/select contacts
                 (k/fields :priority :contactgivenname :contactfamilyname)
                 (k/where {:priority [not= nil]
                           :userid   (user-email->uuid email-address)}))
       (map #(str (:contactgivenname %)
                  " "
                  (:contactfamilyname %)
                  " ("
                  (:priority %)
                  ")"))
       (doall)))

;
; HONEY
;

; Define db access information
(def pg-db
  {:dbtype   "postgresql"
   :dbname   "contactsusers"
   ;:host "mydb.server.com"
   :user     "admin"
   :password "admin"})

(defn user-email->uuid2
  [email-address]
  (->> {:select [:userid]
        :from [:registereduser]
        :where [:= :useremail email-address]}
       (h/format)
       (j/query pg-db)
       (first)
       :userid))

; (j/query db ["select * from items"])


(defn -main
  "I don't do a whole lot."
  [& x]
  (println x "Hello, World!"))
