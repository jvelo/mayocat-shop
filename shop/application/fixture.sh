echo -e "Creating user...\n"

curl -i -H "Content-Type: application/json" -X POST -d " \
  {                                                      \
    \"slug\"   : \"jvelo\",                              \
    \"email\"    : \"jerome@velociter.fr\",              \
    \"password\":\"trololo\"                             \
  }                                                      \
  "                                                      \
  http://localhost:8080/api/users/

echo -e "\n\nAdding some collections...\n"

curl -i -H "Content-Type: application/json"                            \
        -H "Authorization:Basic amVyb21lQHZlbG9jaXRlci5mcjp0cm9sb2xv"  \
        -X POST -d "                                                   \
  {                                                                    \
    \"title\"    : \"Vehicles\"                                        \
  }                                                                    \
  "                                                                    \
  http://localhost:8080/api/collections/

curl -i -H "Content-Type: application/json"                            \
        -H "Authorization:Basic amVyb21lQHZlbG9jaXRlci5mcjp0cm9sb2xv"  \
        -X POST -d "                                                   \
  {                                                                    \
    \"title\"    : \"Garments\"                                        \
  }                                                                    \
  "                                                                    \
  http://localhost:8080/api/collections/

echo -e "\n\nAdding some products...\n"

curl -i -H "Content-Type: application/json"                            \
        -H "Authorization:Basic amVyb21lQHZlbG9jaXRlci5mcjp0cm9sb2xv"  \
        -X POST -d "                                                   \
  {                                                                    \
    \"title\"    : \"Peugeot 403 Convertible\",                        \
    \"onShelf\"  : true,                                               \
    \"price\"    : 7000
  }                                                                    \
  "                                                                    \
  http://localhost:8080/api/products/
curl -i -H "Content-Type: application/json"                            \
        -H "Authorization:Basic amVyb21lQHZlbG9jaXRlci5mcjp0cm9sb2xv"  \
        -X POST -d "                                                   \
  {                                                                    \
    \"title\"    : \"Cortefiel Raincoat\",                             \
    \"onShelf\"  : true,                                               \
    \"price\"    : 200
  }                                                                    \
  "                                                                    \
  http://localhost:8080/api/products/
curl -i -H "Content-Type: application/json"                            \
        -H "Authorization:Basic amVyb21lQHZlbG9jaXRlci5mcjp0cm9sb2xv"  \
        -X POST -d "                                                   \
  {                                                                    \
    \"title\"    : \"Basset Hound\",                                    \
    \"onShelf\"  : true,                                               \
    \"price\"    : 700
  }                                                                    \
  "                                                                    \
  http://localhost:8080/api/products/

echo -e "\n\nAdding some pages...\n"

curl -i -H "Content-Type: application/json"                            \
        -H "Authorization:Basic amVyb21lQHZlbG9jaXRlci5mcjp0cm9sb2xv"  \
        -X POST -d "                                                   \
  {                                                                    \
    \"title\"    : \"James Swasey House\"                              \
  }                                                                    \
  "                                                                    \
  http://localhost:8080/api/pages/
curl -i -H "Content-Type: application/json"                            \
        -H "Authorization:Basic amVyb21lQHZlbG9jaXRlci5mcjp0cm9sb2xv"  \
        -X POST -d "                                                   \
  {                                                                    \
    \"title\"    : \"2003 Grand Prix Americas (Champ Car)\"            \
  }                                                                    \
  "                                                                    \
  http://localhost:8080/api/pages/

echo -e "\n\nAdding some news articles...\n"

curl -i -H "Content-Type: application/json"                            \
        -H "Authorization:Basic amVyb21lQHZlbG9jaXRlci5mcjp0cm9sb2xv"  \
        -X POST -d "                                                   \
  {                                                                    \
    \"title\"    : \"Olfactory sulcus\"                                \
  }                                                                    \
  "                                                                    \
  http://localhost:8080/api/news/
curl -i -H "Content-Type: application/json"                            \
        -H "Authorization:Basic amVyb21lQHZlbG9jaXRlci5mcjp0cm9sb2xv"  \
        -X POST -d "                                                   \
  {                                                                    \
    \"title\"    : \"Alexander Zwo\"                                   \
  }                                                                    \
  "                                                                    \
  http://localhost:8080/api/news/
curl -i -H "Content-Type: application/json"                            \
        -H "Authorization:Basic amVyb21lQHZlbG9jaXRlci5mcjp0cm9sb2xv"  \
        -X POST -d "                                                   \
  {                                                                    \
    \"title\"    : \"World Day for International Justice\"             \
  }                                                                    \
  "                                                                    \
  http://localhost:8080/api/news/
curl -i -H "Content-Type: application/json"                            \
        -H "Authorization:Basic amVyb21lQHZlbG9jaXRlci5mcjp0cm9sb2xv"  \
        -X POST -d "                                                   \
  {                                                                    \
    \"title\"    : \"Hrebenne, Hrubieszów County\"                     \
  }                                                                    \
  "                                                                    \
  http://localhost:8080/api/news/
