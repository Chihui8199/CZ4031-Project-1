# CZ4031-Project-1


# How to commit code

1. Ensure you are on the main branch by doing 
```
git checkout main
```
To check you are on the main branch
```
git branch
```
2. Create you own branch to implement your `feature`/`bug`/`enhancement` etc
```
git checkout -b yourname/feature-add-button
```
3. Add all your changes and commit to this branch. Once pushed and ready, go to the repository page and create a pull request.
Once your code is approved you can merge it to the main branch through github UI/ terminal.
Note: the main branch is a protected branch so that our changes don't overwrite
- If this is too inconvenient we can discuss this again

4. If there are merge conflicts on your pull request, go to your own branch and rebase it off master. This will bring the changes
that others have implemented into your own branch. Your will have to resolve the merge conflict on your code editor
```
git rebase master
```
### Happy Hacking!

