# plot from plink output .fmendel 
filename<-'calls_for_imputation_v3.vdj_rm.fmendel'
SVnumber=8650
library(ggplot2)
ME <- read.fwf(filename,header=FALSE,widths=c(7,14,14,7,5),skip=1)
colnames(ME)<-c('FID','PAT','MAT','CHLD','N')
ME$ID<-paste(ME$PAT,ME$MAT)
me<-ggplot(ME,aes(x=reorder(ID,N),N))
me<-me+geom_point(aes(colour = ID))+ theme(axis.text.x=element_blank())
me <- me + scale_y_continuous(sec.axis = sec_axis(~./SVnumber,name = "Rate"))
me<- me+labs(x = "TRIO")
me
