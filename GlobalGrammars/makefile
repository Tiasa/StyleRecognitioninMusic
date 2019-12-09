JFLAGS=-g
JC=javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        Rule.java \
        Grammar.java \
        Pattern.java \
        SuffixTree.java \
        Greedy.java \
        MostFrequent.java \
        GenerateCFG.java \
        GenerateCFGWithDictionary.java  


default: 
	classes
classes: 
	$(CLASSES:.java=.class)
clean:
	$(RM) *.class
