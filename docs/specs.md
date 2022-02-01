# Spécification fonctionnelles

Auteur : Voltariuss (chef de projet et développeur)

## Introduction

Dans un contexte de re-lancement du serveur Minecraft "Diagonia", le besoin de proposer une interface graphique simple,
ergonomique et adaptée aux fonctionnalités principales du jeu se fait ressentir.

Ce programme intègrera également l'ajout de fonctionnalités supplémentaires pilotées principalement par les interfaces
graphiques en jeu. Cela étant précisé, ces mêmes fonctionnalités devront pouvoir être exploitées à travers l'exécution
de commandes en jeu.

### Cadre et limitations

L'objectif du produit final est double :

* Mettre en à disposition des joueurs finaux du serveur une interface graphique intégrée au jeu Minecraft **sans
  utilisation de mods** (donc aucune modification du client).
* Développer des fonctionnalités centrales pour l'expérimentation du gameplay de Diagonia en jeu.

### Les interfaces graphiques dans Minecraft

Les interfaces graphiques évoquées correspondent en réalité à des inventaires d'objets en jeu avec des propriétés
particulières : la tentative de récupération d'un item par un simple click peut être annulée et entraîner l'exécution d'
actions prédéterminées par exemple. Par ce détournement de la fonctionnalité des inventaires de Minecraft, il est
possible de réaliser des actions au clic, afficher des informations à l'utilisateur ou des réalisations plus complexes
comme un système de stockage virtuel d'items. Les saisies clavier utilisateurs correspondent à un cas spécifique dans ce
contexte : il peut être nécessaire de fermer l'inventaire pour demander une saisie à travers le chat du joueur ou bien
en simulant le placement d'un panneau dans lequel l'utilisateur peut saisir des données.

Il est clair que cette approche souffre de limitations : réaliser des inventaires complexes peut vite devenir un réel
défi en plus d'être coûteux à mettre en place. Également, il est impossible d'y retrouver tout le confort d'une réelle
interface graphique. Pour surmonter ces limites, il faudrait opter pour une approche de développement incluant la
modification du client (jeu lancé par l'utilisateur sur son ordinateur). Ce n'est pas le cas pour ce plugin.

### Environnement d'exécution

Le programme développé devra pouvoir être lancé par un serveur Minecraft, serveur fourni par Mojang gratuitement. Le
serveur Minecraft ne pouvant être modifié sans inspections avancées du code source et/ou l'utilisation de librairies
conçus pour cet usage (comme les Mixins), il est fréquent d'opter pour une surcouche au serveur proposant toute une API
permettant la modification du comportement du serveur. Dans le cadre de ce projet, Bukkit est la surcouche utilisée. En
réalité, il s'agit de PaperMC qui est utilisé aujourd'hui. Ce serveur embarque toute l'API de Bukkit tout en offrant des
outils supplémentaires et des optimisations améliorant les performances générales du serveur.

## Spécifications fonctionnelles

### Cas d'usage

Les fonctionnalités suivantes doivent être possibles pour les joueurs :

* Consulter la liste des shops existants
* Consulter les détails d'un shop existant (nom du propriétaire, liste des métiers exercés par le propriétaire, etc)
* Créer son propre shop
* Modifier son shop
* Désactiver son shop

### Besoins fonctionnels

#### Création d'un shop


## Spécifications non-fonctionnelles

### Technologies utilisées

La création de ce programme se déroulera à l'aide du langage de programmation `Java` accompagné du gestionnaire de
dépendances et de build `Maven`. L'outil `google-java-format` sera utilisé comme formatteur du code Java écrit pour
améliorer la lisibilité de celui-ci.
`SonarQube` sera également utilisé comme analyseur de code afin de détecter d'éventuels bugs, les mauvaises pratiques,
les risques de sécurité ou encore pour pouvoir évaluer facilement la couverture de code suivant les données collectées
par le programme `JaCoCo`. Pour les tests, `JUnit 5` sera utilisé pour l'écriture des tests unitaires et d'
intégration. `Mockito` aura quant à lui pour rôle de permettre la mise en place du mécanisme de "Mockage", mécanisme
nécessaire dans le contexte de développement d'un plugin Bukkit.
`Guice` permettra la mise en place du pattern de conception d'inversion de contrôle à travers l'injection de
dépendances. Ce pattern permet d'améliorer l'organisation, la lisibilité et la maintenabilité/évolutivité du code ainsi
produit.
`Hybernate` assure de son côté l'implémentation d'un ORM afin de faciliter la formulation de requête SQL avec la base de
données du programme pour peu d'efforts.
`SLF4J` sera utilisé comme API de logging accompagné de l'implémentation `Log4j`. Le programme sera compatible pour une
utilisation avec `SQLight` et `MariaDB`. Le choix du SGBD à utiliser sera réservé au propriétaire du serveur Minecraft.

### Critère qualité (norme ISO 9126)

Les critères de qualité présentés par la norme ISO 9126 permettent d'évaluer la qualité d'un système informatique à
travers un ensemble de critères.

En raison des coûts bien trop importants pour réaliser un logiciel répondant à toutes les exigences de la norme et de
l'absence d'intérêt de certains critères, seules les suivants seront pris en compte dans le cadre du développement
de l'application :
* Capacité fonctionnelle : est-ce que le logiciel répond aux besoins fonctionnels ?
* Fiabilité : capacité à maintenir son niveau de service au cours du temps, peu importe les conditions
* Utilisabilité / Facilité d'utilisation : est-ce que le logiciel requiert peu d'efforts pour être utilisé ?
* Rendement et efficacité : 
* Maintenabilité : capacité à ajouter du fonctionnel ou corriger des bugs aisément

Ici la portabilité n'est pas prise en compte puisque le programme doit opérer uniquement en tant que plugin Bukkit.

### Critères d'ergonomie

Les critères d'ergonomie des interfaces graphiques cités dans cette section proviennent d'une liste établie par des
professionnels du domaine et communément adopté dans la communauté du développement logiciel. Plus d'informations sur
ces critères peuvent être retrouvés dans le compte rendu des travaux de recherche menés par J. M. Christian Bastien et
Dominique L. Scapin de l'INRIA sur l'établissement de ces critères. Le document est
accessible [ici](https://www.usabilis.com/wp-content/uploads/2017/09/Criteres-Ergonomiques-pour-l-Evaluation-d-Interfaces-Utilisateur-Scapin-Bastien.pdf)
.

#### Guidage

Le guidage de l'utilisateur lors de son interaction avec l'interface graphique est primordiale pour en faciliter son
apprentissage et son utilisation.

##### Incitation

L'interface graphique étant au service de l'utilisateur la manipulant, l'ajout d'incitations durant l'accomplissement
d'une tâche est nécessaire puisque la plus-value apportée peut être considérable. Cela est particulièrement vrai
pour de nouveaux utilisateurs peu-expérimentés. Dans le contexte du projet Diagonia, cela est encore plus pertinent
puisque la cible inclut des enfants potentiellement en bas âge ou encore des personnes peu expérimentées avec l'usage
de l'informatique.

L'incitation au sein d'une interface graphique consiste à accompagner l'utilisateur sur la tâche qu'il doit réaliser
en donnant des indices sur les actions attendues, l'état exact du système ou encore 

Cela peut passer par :
* La spécification d'un titre pertinent pour chaque fenêtre
* Un bon nommage de chaque bouton, suivi d'une description si nécessaire
* Un ou plusieurs exemples pour réaliser l'action souhaitée par l'utilisateur

##### Feedback

Aspect primordial dans la conception d'une interface graphique, le feedback permet à l'utilisateur de connaître du
système à tout moment lors de son interaction avec le système. Cela passe par :
* Le déclenchement d'une erreur quand quelque chose ne s'est pas déroulé correctement
* L'ajout d'un spinner ou concept similaire pour indiquer que le système est en train de travailler en arrière-plan
et que l'utilisateur doit attendre. L'idéal étant d'accompagner cette illustration d'attente par l'indication des
étapes suivies par le système en arrière, la mise en place d'une barre de progression ou quelque chose de similaire.
C'est particulièrement efficace lorsque le système rencontre une erreur qui ne remonte pas à l'utilisateur afin que
celui-ci puisse identifier facilement quand interrompre le processus actuellement suivi pour éventuellement le relancer,
le signaler aux développeurs ou autre.
* L'ajout d'un timeout côté système lors des phases de calculs : il est inutile de faire attendre l'utilisateur
plusieurs minutes alors que le système est supposé répondre en quelques secondes tout au plus.

#### Adaptabilité

L'interface doit s'adapter au contexte de l'utilisateur. Dans le cadre de l'application, cela peut passer par la mise à
disposition d'un bouton d'achat d'un shop pour les joueurs sans shop tandis que les propriétaires auront à la place un
bouton d'édition de celui-ci. Il n'est ni pertinent d'offrir la possibilité d'éditer un shop qu'un utilisateur ne
possède pas encore tout comme celle d'acheter un shop pour un joueur qui en possède déjà un.

L'élaboration de l'interface graphique sera également réalisée en proposant plusieurs moyens d'accomplissement d'une
tâche donnée. Cela peut passer notamment par la mise à disposition de commandes comme alternative à l'interface
graphique ou encore la proposition de plusieurs moyens de navigation au sein de l'interface graphique notamment avec la
mise à disposition de raccourcis (/shop pour accéder directement au menu des shops sans avoir à passer par le menu
principal préalablement).

#### Gestion des erreurs

Un point d'honneur sera accordé dans la gestion des erreurs :

* Protection contre les erreurs : des mécanismes seront présents pour réduire le risque que l'utilisateur fournisse des
  informations invalides comme la demande de confirmation si les conséquences d'une action peuvent être importantes.
* Qualité des messages d'erreur : quand une erreur survient, un feedback doit être réalisé avec un message expliquant
  que l'action souhaitée n'a pas pu être réalisé, les raisons de cette erreur et des moyens pour l'utilisateur de
  corriger sa saisie. Les messages devront par ailleurs être brefs et adopter un ton neutre
  (pas de blâme, ni d'humour).
* Correction des erreurs : l'utilisateur ne doit pas être pénalisé en cas de saisie incorrecte ou l'être le moins
  possible.
