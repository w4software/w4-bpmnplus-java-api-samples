Pour exécuter les exemples il est nécessaire d'ajouter des dépendences
vers les jars bpmnEngine_client.jar et w4Common_client.jar.
Il est nécessaire également d'avoir un BPMN+ Engine à disposition.

Pensez à éditer les paramètres de connexion vers le serveur dans l'interface SampleConstants (dans le cas
où l'installation du moteur n'utilise pas les paramètres par défaut).

Un fichier BPMN est joint dans resources/eu/w4/sample/MyFirstBpmn.bpmn
L'image bpmn.png illustre le processus modélisé.

L'ordre logique de découverte des exemples est :
- ConnectionAndAuthentication
- CreateUser (exécutable une seule fois, dès la seconde exécution une erreur se produira indiquant que l'utilisateur existe déjà)
- SendBpmnToServer (exécutable une seule fois, dès la seconde exécution un erreur indiquera que le BPMN est déjà importé)
- BpmnRuntimeStuff (nécessite l'exécution préalable du test SendBpmnToServer ou l'envoi du BPMN MyFirstBpmn.bpmn sur le serveur par un autre moyen)
- SearchUserTasks
