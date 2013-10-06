(ns reditore.core
  "Redis session storage."
  (:import java.util.UUID
           ring.middleware.session.store.SessionStore)
  (:require [taoensso.carmine :as car :refer (wcar)]
            [clojure.data.json :as json]))

(defn new-session-key [prefix]
  (str prefix ":" (str (UUID/randomUUID))))

(defn read-session
  [redis-connection session-key data-reader-fn]
  (when session-key
    (when-let [data-str (wcar redis-connection (car/get session-key))]
      (data-reader-fn data-str))))

(defn write-session
  [redis-connection prefix expiration session-key data data-writer-fn]
  (let [session-key (or session-key (new-session-key prefix))
        data-string (data-writer-fn data)]
    (if expiration
      (wcar redis-connection (car/setex session-key expiration data-string))
      (wcar redis-connection (car/set session-key data-string)))
    session-key))

(defn delete-session
  [redis-connection session-key]
  (wcar redis-connection (car/del session-key))
  nil)

; Compatible redis session store with plain serialized json
(deftype JsonSessionRedisStore [redis-connection prefix expiration]
  SessionStore

  (read-session [store session-key]
    (read-session redis-connection
                  session-key
                  json/read-json))

  (write-session [store session-key data]
    (write-session redis-connection
                   prefix
                   expiration
                   session-key
                   data
                   json/write-str))

  (delete-session [store session-key]
    (delete-session redis-connection
                    session-key)))

(defn json-session-redis-store
  ([redis-server]
    (json-session-redis-store redis-server {}))
  ([redis-server {:keys [prefix expire-secs] :or {prefix "session"}}]
    (JsonSessionRedisStore. redis-server prefix expire-secs)))


; Only clojure session which allow to store any clojure object
(deftype SessionRedisStore [redis-connection prefix expiration]
  SessionStore

  (read-session [store session-key]
    (read-session redis-connection
                  session-key
                  read-string))

  (write-session [store session-key data]
    (write-session redis-connection
                   prefix
                   expiration
                   session-key
                   data
                   #(binding [*print-dup* true] (print-str %))))

  (delete-session [store session-key]
    (delete-session redis-connection
                    session-key)))

(defn session-redis-store
  ([redis-server]
    (session-redis-store redis-server {}))
  ([redis-server {:keys [prefix expire-secs] :or {prefix "session"}}]
    (SessionRedisStore. redis-server prefix expire-secs)))