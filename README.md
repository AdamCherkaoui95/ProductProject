# ProductProject
=======
## Description du Projet

Ce projet est une application de gestion de produits permettant de :
- Ajouter un produit
- Récupérer la liste des produits avec pagination et filtres
- Supprimer un produit de manière logique (le produit est marqué comme supprimé sans être effacé de la base de données)
- Supprimer un produit de manière physique (le produit est définitivement supprimé de la base de données)
- Générer automatiquement un code unique pour chaque produit ajouté.

### Fonctionnalités Clés :
1. **Ajout de produit** : Permet d'ajouter un nouveau produit en renseignant les informations nécessaires (nom, description, prix, etc.), avec génération automatique d'un code produit unique.
2. **Récupération de produits avec pagination et filtres** : Offre la possibilité de récupérer la liste des produits en fonction de critères tels que le nom, la catégorie ou la disponibilité, tout en appliquant la pagination pour limiter le nombre de résultats par page.
3. **Suppression logique de produit** : Marque le produit comme supprimé dans la base de données (le produit n'est plus visible mais les données restent accessibles pour d'éventuels besoins futurs).
4. **Suppression physique de produit** : Supprime définitivement un produit de la base de données.
5. **Documentation API via Swagger** : L'application propose une documentation interactive des API à travers Swagger, disponible à l'adresse suivante :  
   [Swagger UI - Documentation API](http://localhost:9096/swagger-ui/index.html#/).
6. **Tests unitaires** : Tous les tests unitaires pour les différentes fonctionnalités sont disponibles et peuvent être exécutés pour vérifier l'intégrité du code.
7. **Démonstration Postman** : Une collection Postman est disponible pour tester les API directement.

