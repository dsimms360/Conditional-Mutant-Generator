# Conditional-Mutant-Generator
This project will generate conditional boundary mutations for a given target file.

The project is currently taliored to generating mutants for the Kotlin conditional operators.

https://www.canva.com/design/DAE8l5Udj8M/DllRC4uTvPi1jNmGpXFEIQ/view?utm_content=DAE8l5Udj8M&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton

# How to run:
Once you have the project installed,
1) Add a copy of the target file to be mutated to the project folder.
2) Navigate to the MutantGenerator.java file within the src folder.
3) Update the filename variable.
4) Run

You will find all of the generated mutant files in the same directory that you placed the copy of the target file.

# What to do after mutants are generated:
If you are using this tool for performing mutation testing, it can manually be done by:
1) Making a copy of the project being tested.
2) Replacing the target file with the generated mutant(s). 
3) Run tests


# Future Updates
In the future, it is the intention for this tool to perform mutation testing on Kotlin Android applications.
Thus, the following will need to be implemented:
* More mutation operators
* Mutation Generation for a given project/package
* Generate a mutation score report
