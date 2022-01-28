# Spécification fonctionnelles

Autheur : Voltariuss (chef de projet et développeur)

## Introduction
Dans un contexte de relancement du serveur Minecraft "Diagonia", le besoin de proposer une interface graphique simple,
ergonomique et adaptée aux fonctionnalités principales du jeu se fait ressentir.

Ce programme intègrera également l'ajout de fonctionnalités supplémentaires pilotées principalement par les interfaces graphiques en jeu. Cela étant précisé, ces mêmes
fonctionnalités devront pouvoir être exploitées à travers l'exécution de commandes en jeu.

### Cadre et limitations

L'objectif du produit final est double :
* Mettre en à disposition des joueurs finaux du serveur une interface graphique intégrée au jeu Minecraft **sans utilisation de mods** (donc aucune modification du client).
* Développer des fonctionnalités centrales pour l'expérimentation du gameplay de Diagonia en jeu.

### Les interfaces graphiques dans Minecraft

Les interfaces graphiques évoquées correspondent en réalité à des inventaires d'objets en jeu avec des propriétés particulière : la tentative de récupération d'un item
par un simple click peut être annulée et entraîner l'exécution d'action prédéterminées par exemple. Par ce détournement de la fonctionnalité des inventaires de Minecraft,
il est possible de réaliser des actions au clic, afficher des informations à l'utilisateur ou des réalisations plus complexes comme un système de stockage virtuel d'items.
Les saisies clavier utilisateurs correspondent à un cas spécifique dans ce contexte : il peut être nécessaire de fermer l'inventaire pour demander une saisie à travers le
chat du joueur ou bien en simulant le placement d'un panneau dans lequel l'utilisateur peut saisir des données.

Il est clair que cette approche souffre de limitations : réaliser des inventaires complexes peut vite devenir un réel défi en plus d'être coûteux à mettre en place. Également,
il est impossible d'y retrouver tout le confort d'une réelle interface graphique. Pour surmonter ces limites, il faudrait opter pour une approche de développement incluant
la modification du client (jeu lancé par l'utilisateur sur son ordinateur). Ce n'est pas le cas pour ce plugin.

### Environnement d'exécution

Le programme développé devra pouvoir être lancé par un serveur Minecraft, serveur fourni par Mojang gratuitement. Le serveur Minecraft ne pouvant être modifié sans inspections
avancées du code source et/ou l'utilisation de librairies conçus pour cet usage (comme les Mixins), il est fréquent d'opter pour une surcouche au serveur proposant toute
une API permettant la modification du comportement du serveur. Dans le cadre de ce projet, Bukkit est la surcouche utilisée. En réalité, il s'agit de PaperMC qui est
utilisé aujourd'hui. Ce serveur embarque toute l'API de Bukkit tout en offrant des outils supplémentaires et des optimisations améliorant les performances générales
du serveur.

## Spécifications fonctionnelles

### Shops de joueurs

Les fonctionnalités suivantes doivent être possibles pour les joueurs :
* Consulter la liste des shops existants
* Consulter les détails d'un shop existant (nom du propriétaire, liste des métiers exercés par le propriétaire, etc)
* Créer son propre shop
* Modifier son shop (dont pouvoir le désactiver)

## Spécifications non-fonctionnelles

### Technologies utilisées

La création de ce programme se déroulera à l'aide du langage de programmation `Java` accompagné du gestionnaire de dépendances et de build `Maven`.
L'outil `google-java-format` sera utilisé comme formatteur du code Java écrit pour améliorer la lisibilité de celui-ci.
`SonarQube` sera également utilisé comme analyseur de code afin de détecter d'éventuels bugs, les mauvaises pratiques, les risques de sécurité ou encore pour pouvoir
évaluer facilement la couverture de code suivant les données collectées par le programme `JaCoCo`.
Pour les tests, `JUnit 5` sera utilisé pour l'écriture des tests unitaires et d'intégration. `Mockito` aura quand à lui pour rôle de permettre
la mise en place du méchanisme de "Mockage", méchanisme nécessaire dans le contexte de développement d'un plugin Bukkit.
`Guice` permettra la mise en place du pattern de conception d'inversion de contrôle à travers l'injection de dépendances. Ce pattern permet d'améliorer l'organisation,
la lisibilité et la maintenabilité/évolutivité du code ainsi produit.
`Hybernate` assure de son côté l'implémentation d'un ORM afin de facilier la formulation de requête SQL avec la base de données du programme pour peu d'efforts.
`SLF4J` sera utilisé comme API de logging accompagné de l'implémentation `Log4j`.
 Le programme sera compatible pour une utilisation avec `SQLight` et `MariaDB`. Le choix du SGBD à utiliser sera réservé au propriétaire du serveur Minecraft.
 
 ### Ergonomie
 
 Les critères d'ergonomie des interfaces graphiques cités dans cet section proviennent d'une liste établies par des profesionnels du domaine et communémant adopté
 dans la communauté du développement logiciel. Plus d'informations sur ces critères peuvent être retrouvés dans le compte rendu des travaux de recherche menés par
 J. M. Christian Bastien et Dominique L. Scapin de l'INRIA sur l'établissement de ces critères.
 Le document est accessible [ici](https://www.usabilis.com/wp-content/uploads/2017/09/Criteres-Ergonomiques-pour-l-Evaluation-d-Interfaces-Utilisateur-Scapin-Bastien.pdf).
 
 
 
 #### Guidage
 
 
 
 #### Adaptabilité
 
 L'interface doit s'adapter au contexte de l'utilisateur. Dans le cadre de l'application, cela peut passer par la mise à disposition d'un bouton d'achat d'un shop pour les
 joueurs sans shop tandis que les propriétaires auront à la place un bouton d'édition de celui-ci. Il n'est ni pertinent d'offrir la possibilité d'éditer un shop qu'un
 utilisateur ne possède pas encore tout comme celle d'acheter un shop pour un joueur qui en possède déjà un.
 
 L'élaboration de l'interface graphique sera également réalisée en proposant plusieurs moyens d'accomplissement d'une tâche donnée. Cela peut passer notamment par la mise
 à disposition de commandes comme alternative à l'interface graphique ou encore la proposition de plusieurs moyens de navigation au sein de l'interface graphique
 notamment avec la mise à disposition de raccourcis (/shop pour accéder directement au menu des shops sans avoir à passer par le menu principal préalablement).
 
 #### Gestion des erreurs
 
 Un point d'honneur sera accordé dans la gestion des erreurs :
 * Protection contre les erreurs : des méchanismes seront présents pour réduire le risque que l'utilisateur fournisse des informations invalides.
 * Qualité des messages d'erreur : quand une erreur survient, un feedback doit être réalisé avec un message expliquant que l'action souhaité n'a pas pu être réalisé,
 les raisons de cette erreur et des moyens pour l'utilisateur de corriger sa saisie. Les messages devront par ailleurs être brefs et adopter un ton neutre
 (pas de blâme, ni d'humour).
 * Correction des erreurs : l'utilisateur ne doit pas être pénalisé en cas de saisie incorrecte, ou l'être le moins possible.
