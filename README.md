w4-bpmnplus-java-api-samples
============================

This project contains various examples of code that will show how to interact with W4 BPMN+ Engine using its Java APIs


Getting the samples
-------------------

You may download it as a [zip](https://github.com/w4software/w4-bpmnplus-java-api-samples/archive/master.zip) or directly using git with the following command

    git clone https://github.com/w4software/w4-bpmnplus-java-api-samples.git


Usage (EN)
----------

To be able to run the samples, you will need following two dependencies `bpmnEngine_client.jar` et `w4Common_client.jar`.

You also need an W4 BPMN+ Engine installed and ready.

You may need to edit connection parameters to the engine in interface `SampleContants` in case it is not using default parameters.

You'll find a small process file enclosed in `resources/eu/w4/sample/MyFirstBpmn.bpmn`. An image version displaying it is in bpmn.png

Logical and suggested order discovering sample files is the following:

- ConnectionAndAuthentication
- CreateUser (_runnable only once and will fail telling the user is already created starting from second run_)
- SendBpmnToServer (_runnable only once and will fail telling the process is already imported starting from second run_)
- BpmnRuntimeStuff (_will run provided that you've already imported MyFirstBpmn.bpmn on the engine using `SendBpmnToServer` or any other available mean_)
- SearchUserTasks

Utilisation (FR)
----------------

Pour exécuter les exemples il est nécessaire d'ajouter des dépendences
vers les jars `bpmnEngine_client.jar` et `w4Common_client.jar`.

Il est nécessaire également d'avoir un W4 BPMN+ Engine à disposition.

Pensez à éditer les paramètres de connexion vers le serveur dans l'interface `SampleConstants` (dans le cas où l'installation du moteur n'utilise pas les paramètres par défaut).

Un fichier BPMN est joint dans `resources/eu/w4/sample/MyFirstBpmn.bpmn`
L'image bpmn.png illustre le processus modélisé.

L'ordre logique de découverte des exemples est :

- ConnectionAndAuthentication
- CreateUser (_exécutable une seule fois, dès la seconde exécution une erreur se produira indiquant que l'utilisateur existe déjà_)
- SendBpmnToServer (_exécutable une seule fois, dès la seconde exécution un erreur indiquera que le BPMN est déjà importé_)
- BpmnRuntimeStuff (_nécessite l'exécution préalable du test `SendBpmnToServer` ou l'envoi du BPMN MyFirstBpmn.bpmn sur le serveur par un autre moyen_)
- SearchUserTasks

Go further
----------

You will find more information about the product and its APIs on [www.w4software.com](http://www.w4software.com) and on the [product documentation](http://docs.w4store.com).

Vous trouverez plus d'information concernant le produit sur [le site de l'éditeur](http://www.w4software.com) et dans la [documentation produit](http://docs.w4store.com).

License
-------

Copyright (c) 2014, W4 S.A. 

This project is licensed under the terms of the MIT License (see LICENSE file)

Ce projet est licencié sous les termes de la licence MIT (voir le fichier LICENSE)
