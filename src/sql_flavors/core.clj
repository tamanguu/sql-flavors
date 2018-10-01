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
                           :userid   (user-email->uuid email-address)}))))

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

(def query
  (partial j/query pg-db))

(defn user-email->uuid2
  [email-address]
  (-> {:select [:userid]
       :from [:registereduser]
       :where [:= :useremail email-address]}
      (h/format)
      (query)
      (first)
      :userid))

(defn contacts-with-priority2
  [email-address]
  (-> {:select [:priority :contactgivenname :contactfamilyname]
       :from [:contacts]}
      (merge-where [:= :userid (user-email->uuid2 email-address)])
      (merge-where [:not= :priority nil])
      (h/format)
      (query)))


;
; HELPERS
;

(defn display-contacts-with-priority
  [contacts]
  (doseq [c contacts]
    (println (str (:contactgivenname c)
                  " "
                  (:contactfamilyname c)
                  " ("
                  (:priority c)
                  ")"))))

(defn -main
  "I don't do a whole lot."
  [& x]
  (println "-+-+-+-+-+ KORMA +-+-+-+-+-")
  (display-contacts-with-priority (contacts-with-priority "dr@t.de"))
  (println "")
  (println "-+-+-+-+-+ HONEY +-+-+-+-+-")
  (display-contacts-with-priority (contacts-with-priority2 "dr@t.de")))
