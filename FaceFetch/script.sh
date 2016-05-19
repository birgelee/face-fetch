num=1
while read p; do
  curl $p -o $num.jpg
  num=$((num+1))
done <links.txt