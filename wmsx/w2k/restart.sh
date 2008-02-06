rm -rf atype && \
mkdir atype && \
cd atype && \
tar -xzf ~/originals/atype/atype-30.tar && \
cp atype.vsp_st atype.vsp && \
touch atype.broyd1 && \
touch atype.broyd2 && \
touch atype.scf && \
cd .. && \
./generate.sh atype
