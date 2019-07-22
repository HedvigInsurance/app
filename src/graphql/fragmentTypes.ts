export default {
    __schema: {
        types: [
            {
                kind: "UNION",
                name: "Incentive",
                possibleTypes: [
                    {
                        name: "FreeMonths"
                    },
                    {
                        name: "MonthlyCostDeduction"
                    }
                ]
            }
        ]
    }
}