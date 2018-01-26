# coding=UTF-8

from datetime import datetime
from common._common_orm import linesOfNumbersSeriesCursor
from ru.curs.celesta import CelestaException


def getNextNoOfSeries(context, seriesId, linesOfNumbersSeries=None, updateNum=True):
    lonsFlag = False
    if linesOfNumbersSeries is None:
        linesOfNumbersSeries = linesOfNumbersSeriesCursor(context)
        lonsFlag = True
    linesOfNumbersSeries.setRange('seriesId', seriesId)
    linesOfNumbersSeries.setRange('isOpened', True)
    linesOfNumbersSeries.setRange('startingDate',
                                  datetime.strptime('1900-01-01', '%Y-%m-%d'),
                                  datetime.today())
    if linesOfNumbersSeries.count() > 1:
        raise Exception("There is more than one opened line in the series '%s'" % seriesId)

    if linesOfNumbersSeries.tryFindSet():
        seriesObject = GettingNextNumberOfSeries(seriesId,
                                                 linesOfNumbersSeries.lastUsedNumber,
                                                 linesOfNumbersSeries.startingNumber,
                                                 linesOfNumbersSeries.endingNumber,
                                                 linesOfNumbersSeries.incrimentByNumber,
                                                 linesOfNumbersSeries.isFixedLength)
        nextNum = seriesObject.getNextNum()
        linesOfNumbersSeries.lastUsedNumber = int(nextNum)
        linesOfNumbersSeries.lastUsedDate = datetime.today()
        if updateNum:
            linesOfNumbersSeries.update()
        prefix = linesOfNumbersSeries.prefix
        postfix = linesOfNumbersSeries.postfix
        if lonsFlag:
            linesOfNumbersSeries.close()
        return '%s%s%s' % (prefix, nextNum, postfix)
    else:
        CelestaException("There are no available opened lines in the series '%s'!" % seriesId)


class GettingNextNumberOfSeries():
    def __init__(self, seriesId, lastUsed, startNum=0, endNum=0, incr=1, isFixedLength=True):
        self.seriesId = seriesId
        self.startNum = startNum
        self.endNum = endNum
        self.lastUsed = lastUsed
        self.isFixedLength = isFixedLength
        self.incr = incr if incr >= 1 else 1

        if int(self.startNum) > int(self.endNum):
            raise Exception('Min value is greater than max value for series %s' % self.seriesId)

    def getNextNum(self):
        """Finding next num"""
        if not self.lastUsed:
            return unicode(self.startNum)
        elif self.lastUsed < self.endNum:
            nextNum = unicode(self.lastUsed + self.incr)
            if int(nextNum) > self.endNum:
                raise Exception('Next number value is greater than max value for series %s' % self.seriesId)
            if self.isFixedLength:
                nextNum = '0' * (len(unicode(self.endNum)) - len(nextNum)) + nextNum
                """If nextNum == '2' but it should be like '0002' """

        else:
            raise CelestaException('Last used value is greater than max value for series %s' % self.seriesId)

        return nextNum
