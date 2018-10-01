(defproject sql-flavors "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [org.postgresql/postgresql "42.1.4"]
                 [korma "0.4.3"]
                 [honeysql "0.9.3"]]
  :main ^:skip-aot sql-flavors.core)
