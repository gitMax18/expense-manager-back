# Description

Cette application sert a gérer ces dépenses en enregistrant les entrés et sorties sur un ou plusieurs comptes associer a un utilisateur.

# Fonctionnalitées

- Chaque utilisateur pourra avoir des comptes
- Possibilité d'avoir des revenues mensuel (salaire, loyer etc...)
- Possibilité d'avoir des dépense mensuel (loyer, assurance, pret etc...)
- Chaque dépense aura ca catégorie

# Consignes de code

## Générale

- Programmation fonctionnelles
- Langue anglais

## Controller

- Les response devront etre au format AppResponse
- Les réponse devront etre des DTO
- Le mapping vers les DTO se fait au niveau des controllers

## DTO

- Les DTO ont leurs propres dossier
- Les mapper seront dans le meme dossier que les dto
- Les DTO utilisent des records

## Entité

- Les Id devront utiliser GenerationType.IDENTITY
- Utiliser LOMBOK pour la génération des entités/models
- Chaque entité devra étendre BaseEntity

## Test

- Les tests utiliseront Junit et mockito
- Les tests concernant la base de données devront etres fait avec test container
- Les tests concernant des formulaires devront également vérifier que les erreurs sont bien gérer avec renvoi de key value

## CI/CD

- 3 branches sont gérées par la CI/CD, dev (execute les tests), staging (test et création d'une image pour déploiment en stagging), prod (test et création d'une image pour déploiment en production)

- Utilisation de github actions

# Exécution

- Pour lancer des commandes mvn, il faut utiliser le wrapper ./mvnw a partir de la base du projet.
