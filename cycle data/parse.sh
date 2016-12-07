FILES="journeys/*.txt"
i=0
for f in $FILES
    do
    let i++ 
    java -jar ~/Downloads/hyperstar-without-mathematica/HyperStar.jar -cli -f $f -of $f -bn 1
    echo $i
done