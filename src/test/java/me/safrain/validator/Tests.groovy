package me.safrain.validator

import org.junit.Test

class Tests extends BaseTest {


    @Test
    void manualScope() {
        inspector.validate([
                a: null
        ], {
            V.manual {
                assert !V.string.notEmpty('a')
            }
        } as Validator).with {
            assert !it
        }
    }


    @Test
    void elementTypeViolation() {
        inspector.validate([1, 2, 3]) {
            V.number.isInteger '[0]'
            V.number.isInteger '[1]'
            V.number.isInteger '[2]'
        }.with {
            assert !it
        }
    }

    @Test
    void oneLevel() {
        inspector.validate([
                a  : '',
                b  : '1',
                c  : null,
                '%': 1
        ], {
            V.string.isString('a')
            V.string.notEmpty('/b')
            V.common.isNull('/c')
            V.number.notZero('/%%')
        } as Validator).with {
            assert !it
        }
    }

    @Test
    void nullPath() {
        inspector.validate([a: 1], {
            V.common.isNull('a/b')
        } as Validator).with {
            assert it.size() == 1
        }
    }






    @Test
    void manual() {
        inspector.validate([
                a: null,
                b: '2'
        ], {
            V.manual({
                assert !V.string.isString('a')
                if (V.string.isString('a')) {
                    assert V.string.notEmpty('b')
                }
            })
        }).with {
            assert !it
        }
    }



    @Test
    void scope() {
        inspector.validate([
                a: null,
                b: [
                        c: '1',
                        d: []
                ]]) {
            V.scope('b/c') {
                V.string.notEmpty('/')
                V.common.notNull('d')
            }
        }
    }

    @Test
    void comment() {
        inspector.validate([
                a: null,
                b: 1
        ]) {
            V.common.notNull 'a #a is null'
            V.string.isString 'b #  b is not string'
        }.with {
            assert it.size() == 2
            assert it[0].expression.comment == 'a is null'
            assert it[1].expression.comment == '  b is not string'
        }
    }

    @Test
    void everyProperty() {
        inspector.validate([
                a: '1',
                b: '2',
                c: '3',
        ]) {
            V.string.isString '*'
        }.with {
            assert !it
        }
    }

    @Test
    void everyPropertyEmpty() {
        inspector.validate([
                :
        ]) {
            V.string.isString '*'
            V.string.isString '*/*'
        }.with {
            assert !it
        }
    }

    @Test
    void anyProperty() {
        inspector.validate([
                a: '1',
                b: 2,
                c: '3',
        ]) {
            V.number.isInteger('/?')
        }.with {
            assert !it
        }
    }


    @Test
    void anyPropertyNested() {
        inspector.validate([
                a: '1',
                b: [
                        b1: 1,
                        b2: 2
                ],
                c: '3',
        ]) {
            V.number.isInteger('/?/?')
        }.with {
            assert !it
        }
    }

    @Test
    void iterativePropertyMix() {
        inspector.validate([
                a: [
                        b1: 0,
                        b2: ' '
                ],
                b: [
                        b3: 0,
                        b4: ' '
                ],
                c: [
                        b5: 1,
                        b6: 1,
                ],
        ]) {
            V.number.isInteger('/*/?')
            V.number.isInteger('/?/*')
        }.with {
            assert !it
        }
    }

    @Test
    void arrayAccess() {
        inspector.validate([
                a: ['1', '2', '3'],
                b: [],
                c: [1],
        ]) {
            V.string.notEmpty('a[0]')
//            V.string.notEmpty('a[1]')
//            V.string.notEmpty('a[2]')
//            V.string.notEmpty('a/[2]')
//            V.common.isEquals('a[-1]', '3')
//            V.common.isEquals('a[-2]', '2')
//            V.common.isEquals('c[-1]', 1)
        }.with {
            assert !it
        }
    }



    @Test
    void eachElement() {
        inspector.validate([1, 2, 3]) {
            V.number.isInteger '[*]'
        }.with {
            assert !it
        }
    }



    @Test
    void emptyArray() {
        inspector.validate([]) {
            V.number.isInteger '[*]'
        }.with {
            assert it.size() == 0
        }
    }

    @Test
    void propertyOverArray() {
        inspector.validate([
                [a: 1],
                [a: 1],
                [a: 1]
        ]) {
            V.number.isInteger '[*]/a'
        }.with {
            assert it.size() == 0
        }
    }

    @Test
    void arrayEachSomeWrongType() {
        inspector.validate([1, 2, '3']) {
            V.number.isInteger '[*]'
        }.with {
            assert it.size() == 1
        }
    }

    @Test
    void arrayRange() {
        inspector.validate([1, 2, 3, 4, 5, 6]) {
            V.number.inRange('[2..5]', 3, 6)
        }.with {
            assert !it
        }
        inspector.validate([1, 2, 3, 4, 5, 6]) {
            V.number.inRange('[2..-1]', 3, 6)
        }.with {
            assert !it
        }
        inspector.validate([1, 2, 3, 4, 5, 6]) {
            V.number.inRange('[2..-1]', 5, 6)
        }.with {
            assert it.size() == 1
        }

    }

    @Test
    void arrayRangeSame() {
        inspector.validate([1, 2, 3, 4, 5, 6]) {
            V.common.isEquals('[3..3]', 4)
        }.with {
            assert !it
        }
    }

    @Test
    void nestedIterative() {
        inspector.validate([
                a: [
                        a: [1, 2, 3]
                ],
                b: [
                        a: [1, 2, 3]
                ]
        ]) {
            V.number.isInteger('*/a/[*]')
        }.with {
            assert !it
        }
    }

}
