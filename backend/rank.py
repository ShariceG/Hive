import math

class Rank:

    STAT_WORTH = {
        'post_likes': 1,
        'post_dislikes': -1,
        'comment_likes': 1,
        'comment_dislikes': -1,
        'usage': 1
    }

    # Tweaks how far apart each of the levels are. For example, the closer to
    # zero this is, the further apart the levels.
    LEVEL_RATE = 0.2

    class Stats(object):
        '''Holds info used to calculate amount of xp a player gets'''
        post_likes = 0
        post_dislikes = 0
        comment_likes = 0
        comment_dislikes = 0
        usage = 0

    def __init__(self):
        self._xp = 0
        self._level = 0
        self._label = ''

    def consume_stats(self, stats):
        total_arr = [
            STAT_WORTH['post_likes']*stats.post_likes,
            STAT_WORTH['post_dislikes']*stats.post_dislikes,
            STAT_WORTH['comment_likes']*stats.comment_likes,
            STAT_WORTH['comment_dislikes']*stats.comment_dislikes,
            STAT_WORTH['usage']*stats.usage,
        ]
        self._xp = sum(total_arr)
        self._level = self._calculate_level()

    def _calculate_level(self):
        return math.sqrt(LEVEL_RATE*self._xp)

    def from_model(self):
        pass

    def from_proto(self):
        pass

    def to_model(self):
        pass

    def to_proto(self):
        pass
