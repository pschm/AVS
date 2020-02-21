# Optimized Shopping

Nearly every human is faced with the following problem. You have a list of products you want to buy in a shop and you don't have much time, so you want to hurry up and buy these products as fast as possible, but in the end you forgot one product and you have to go back.

This application is a first version of a solution of that problem. The structure of the shop "Dornseifer" in Gummersbach is implemented and the user is able to create a shopping list. With this list the optimized way through the shop will be calculated and displayed graphically, so that the user is able to buy the products after the optimized calculation.

The current implementation of this project includes:
- Unity as frontend. Here the user can create the shopping list and will get the view of the calculated shortest way through the shop.
- Scheduler is the interface between the unity frontend and the individual workers.
- Worker as working instance. They calculate the shortest way through the shop.

You can find more information about the project in the **docs folder**. A short description about the different instances and their installation is find in the **readme file** in the respective folder.



