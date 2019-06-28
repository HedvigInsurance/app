import { client } from 'src/graphql/client';
import { Insurance } from 'src/graphql/components';
import gql from 'graphql-tag';

const QUERY = gql`
  query OfferingScreenMock {
    insurance {
      insuredAtOtherCompany
      personsInHousehold
      monthlyCost
      address
      presaleInformationUrl
      currentInsurerName
      policyUrl
      status
      activeFrom
      type
      perilCategories {
        title
        description
        iconUrl
        perils {
          id
          title
          imageUrl
          description
        }
      }
    }
  }
`;

interface Typename {
  __typename: string;
}

export const addMockData = () =>
  client.writeQuery<{ insurance: Insurance | Typename }>({
    query: QUERY,
    data: {
      insurance: {
        insuredAtOtherCompany: true,
        personsInHousehold: 2,
        monthlyCost: 129,
        address: 'Mockgatan 12',
        certificateUrl: '',
        presaleInformationUrl: '',
        currentInsurerName: 'IF',
        policyUrl: '',
        status: 'PENDING',
        activeFrom: new Date().toString(),
        type: 'RENT',
        perilCategories: [
          {
            title: 'Test',
            description: '',
            iconUrl: '',
            perils: [
              {
                id: 'ME.ASSAULT',
                title: 'Test',
                imageUrl: '',
                description: 'mock '.repeat(50),
                __typename: 'Peril',
              },
            ],
            __typename: 'PerilCategory',
          },
          {
            title: 'Test',
            description: '',
            iconUrl: '',
            perils: [
              {
                id: 'HOUSE.BRF.WATER',
                title: 'Test',
                imageUrl: '',
                description: 'mock',
                __typename: 'Peril',
              },
            ],
            __typename: 'PerilCategory',
          },
          {
            title: 'Test',
            description: '',
            iconUrl: '',
            perils: [
              {
                id: 'STUFF.CARELESS',
                title: 'Test',
                imageUrl: '',
                description: 'mock',
                __typename: 'Peril',
              },
            ],
            __typename: 'PerilCategory',
          },
        ],
        __typename: 'Insurance',
      },
    },
  });
