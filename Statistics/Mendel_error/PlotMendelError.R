# plot from plink output .fmendel 
filename<-'GS.passed.100k10mconcat.vdj_removed.sorted.IRS.fmendel'
SVnumber=8208
ME <- read.fwf(filename,header=FALSE,widths=c(7,14,14,7,5),skip=1)
colnames(ME)<-c('FID','PAT','MAT','CHLD','N')
me<-ggplot(ME,aes(MAT,N))
me<-me+geom_point()+ theme(axis.text.x=element_blank())
me <- me + scale_y_continuous(sec.axis = sec_axis(~./SVnumber,name = "Rate"))
me<- me+labs(x = "TRIO")
me
