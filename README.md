# What is Reditore

Reditore is Clojure [Ring](https://github.com/ring-clojure/ring) session store implemented on top of redis db. It uses 2.x version of Clojure redis client [Carmine](https://github.com/ptaoussanis/carmine).

Inspiration came from old implmentation [clj-session-store](https://github.com/wuzhe/clj-redis-session) and from mongo session store [Monger](https://github.com/michaelklishin/monger).

# Installation

Add

```
[reditore "0.9.0"]
````

to `:dependencies` in your `project.clj`.

# Usage

`reditore.core` implements two ways how to store data in redis

1. **Clojure mode** `reditore.core/session-redis-store`. It serializes all objects into Clojure form, which can be decode again only in clojure. It is prefered if you dont need to load session in another language/applications.
2. **Compatible mode** `reditore.core/json-session-redis-store`. This serializes data to pure json. Which is easy to load them from any other language/applications.

**Warning** If you switch one mode for another drop all your sessions to prevent application exceptions from loading wrong format.

## With Ring server

```
(ns myapp
  (:require [ring.middleware.session :refer [wrap-session]]
            [reditore.core :refer [session-redis-store]]))

(def redis-connection
{:pool {}
 :spec {:host "127.0.0.1"
		:port 6379}})

(def app
  (-> your-routes
      ... other middlewares ...
      (wrap-session {:store (session-redis-store redis-connection)})
      ... other middlewares ...))
```

## With Noir application
```
(ns myapp
  (:require [noir.util.middleware :refer [app-handler]]
            [reditore.core :refer [session-redis-store]]))


(def redis-connection
{:pool {}
 :spec {:host "127.0.0.1"
		:port 6379}})

(def app
  (app-handler [my-routes]
			   :store (session-redis-store session-redis-server)
			   :middleware [])
```

## Configuration

When you want to use json **compatible mode**

```
# require compatible json-session-redis-store
(:require [reditore.core :refer [json-session-redis-store]])
```
```
# use that 
(json-session-redis-store redis-connection)
```

Want sessions to automatically **expire**?

```
# expire after 12 hours
(session-redis-store redis-connection {:expire-secs (* 3600 12)})
```
Or different **prefix** for redis key?

```
# Change prefix
(session-redis-store redis-connection {:prefix "my-sessions"})
```

# Licence

Reditor is released under the [MIT License](http://www.opensource.org/licenses/MIT).